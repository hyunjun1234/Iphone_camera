package studio.moment.camera.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorSpace
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.os.StatFs
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.color.Looks
import studio.moment.camera.color.compositeOnWhite
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.coroutineContext

data class SavedPhoto(val uri: Uri, val original: Uri?, val model: String)

class PhotoStore(private val context: Context) {
    private val resolver = context.contentResolver
    private val folder get() = File(context.cacheDir, "photos").apply { mkdirs() }

    suspend fun copyImport(uri: Uri): File = withContext(Dispatchers.IO) {
        val file = File.createTempFile("import-", ".image", folder)
        try {
            resolver.openInputStream(uri).use { input ->
                if (input == null) throw UserMessageException("사진을 열 수 없어요. 다른 사진을 선택해 주세요.")
                file.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var total = 0L
                    while (true) {
                        coroutineContext.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > 100L * 1024 * 1024) throw UserMessageException("100MB 이하의 사진을 선택해 주세요.")
                        output.write(buffer, 0, count)
                    }
                }
            }
            file
        } catch (e: Exception) { file.delete(); throw e }
    }

    suspend fun export(source: File, lut: ColorLut, settings: GradeSettings, saveOriginal: Boolean): SavedPhoto = withContext(Dispatchers.IO) {
        // Leave room for rotation, a mutable copy, the UI and camera buffers on small heaps.
        val maxPixels = minOf(16_000_000L, Runtime.getRuntime().maxMemory() / 16).toInt().coerceAtLeast(1_000_000)
        val bitmap = decode(source, maxPixels, mutable = true)
        var output: File? = null
        val created = mutableListOf<Uri>()
        try {
            output = File.createTempFile("graded-", ".jpg", folder)
            // Chunked processing avoids another full-resolution pixel array.
            val rows = 32
            val pixels = IntArray(bitmap.width * rows)
            for (y in 0 until bitmap.height step rows) {
                coroutineContext.ensureActive()
                val height = minOf(rows, bitmap.height - y)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, y, bitmap.width, height)
                compositeOnWhite(pixels, bitmap.width * height)
                lut.apply(pixels, bitmap.width * height)
                bitmap.setPixels(pixels, 0, bitmap.width, 0, y, bitmap.width, height)
            }
            output.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.JPEG, 96, it)) }
            val now = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.US).format(Date())
            val sourceExif = runCatching { ExifInterface(source) }.getOrNull()
            ExifInterface(output).apply {
                listOf(ExifInterface.TAG_MAKE, ExifInterface.TAG_MODEL, ExifInterface.TAG_EXPOSURE_TIME,
                    ExifInterface.TAG_F_NUMBER, ExifInterface.TAG_PHOTOGRAPHIC_SENSITIVITY, ExifInterface.TAG_FOCAL_LENGTH,
                    ExifInterface.TAG_DATETIME_ORIGINAL, ExifInterface.TAG_FLASH, ExifInterface.TAG_WHITE_BALANCE).forEach { tag ->
                    sourceExif?.getAttribute(tag)?.let { setAttribute(tag, it) }
                }
                setAttribute(ExifInterface.TAG_DATETIME, now)
                setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL.toString())
                setAttribute(ExifInterface.TAG_COLOR_SPACE, "1")
                setAttribute(ExifInterface.TAG_SOFTWARE, "moment ${studio.moment.camera.BuildConfig.VERSION_NAME}")
                setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "moment / ${settings.lookId} inspired / intensity=${settings.intensity} warmth=${settings.warmth} tone=${settings.tone}")
                saveAttributes()
            }
            coroutineContext.ensureActive()
            ensureStorage(output.length() + if (saveOriginal) source.length() else 0L)
            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date()) + "_" + UUID.randomUUID().toString().take(8)
            val original = if (saveOriginal) {
                val mime = runCatching { BitmapFactory.Options().also { it.inJustDecodeBounds = true; BitmapFactory.decodeFile(source.path, it) }.outMimeType }.getOrNull() ?: "image/jpeg"
                val extension = when (mime) { "image/png" -> "png"; "image/webp" -> "webp"; "image/heif", "image/heic" -> "heic"; "image/avif" -> "avif"; else -> "jpg" }
                publish(source, "MOMENT_${stamp}_original.$extension", mime, "Pictures/moment/Originals").also(created::add)
            } else null
            val uri = publish(output, "MOMENT_${stamp}_${settings.lookId}.jpg", "image/jpeg", "Pictures/moment").also(created::add)
            SavedPhoto(uri, original, Looks.find(settings.lookId).model)
        } catch (e: Throwable) {
            created.forEach { runCatching { resolver.delete(it, null, null) } }
            throw e
        } finally {
            bitmap.recycle()
            output?.delete()
        }
    }

    suspend fun thumbnail(file: File, lut: ColorLut? = null): Bitmap = withContext(Dispatchers.Default) {
        val bitmap = decode(file, 900_000, mutable = true)
        if (lut != null) {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            compositeOnWhite(pixels)
            lut.apply(pixels)
            bitmap.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        }
        bitmap
    }

    suspend fun thumbnail(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null
            val sample = sampleSize(bounds.outWidth, bounds.outHeight, 900_000)
            resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply {
                inSampleSize = sample; inPreferredConfig = Bitmap.Config.ARGB_8888
                inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
            }) }
        }.getOrNull()
    }

    /** Only our stale work files are removed; user photos and recent in-flight work are untouched. */
    suspend fun cleanStaleWork() = withContext(Dispatchers.IO) {
        val cutoff = System.currentTimeMillis() - TimeUnitDays
        folder.listFiles()?.filter { it.isFile && it.lastModified() < cutoff &&
            listOf("capture-", "import-", "graded-").any { prefix -> it.name.startsWith(prefix) } }?.forEach { it.delete() }
    }

    private fun decode(source: File, maxPixels: Int, mutable: Boolean): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0 || bounds.outMimeType == "image/gif")
            throw UserMessageException("이 사진 형식은 지원하지 않아요. JPG, PNG 등 정지 사진을 선택해 주세요.")
        val sample = sampleSize(bounds.outWidth, bounds.outHeight, maxPixels)
        val bitmap = BitmapFactory.decodeFile(source.path, BitmapFactory.Options().apply {
            inSampleSize = sample; inMutable = mutable; inPreferredConfig = Bitmap.Config.ARGB_8888
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        }) ?: throw UserMessageException("사진을 읽지 못했어요. 다른 사진을 선택해 주세요.")
        val orientation = runCatching { ExifInterface(source).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1) }.getOrDefault(1)
        val matrix = Matrix().apply {
            when (orientation) {
                2 -> setScale(-1f, 1f)
                3 -> setRotate(180f)
                4 -> { setRotate(180f); postScale(-1f, 1f) }
                5 -> { setRotate(90f); postScale(-1f, 1f) }
                6 -> setRotate(90f)
                7 -> { setRotate(-90f); postScale(-1f, 1f) }
                8 -> setRotate(-90f)
            }
        }
        if (matrix.isIdentity) return bitmap
        try {
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated !== bitmap) bitmap.recycle()
            if (mutable && !rotated.isMutable) {
                try { return rotated.copy(Bitmap.Config.ARGB_8888, true) } finally { rotated.recycle() }
            }
            return rotated
        } catch (e: OutOfMemoryError) { bitmap.recycle(); throw e }
    }

    private fun sampleSize(width: Int, height: Int, maxPixels: Int): Int {
        var sample = 1
        while (width.toLong() * height / (sample.toLong() * sample) > maxPixels) sample *= 2
        return sample
    }

    private fun ensureStorage(bytes: Long) {
        val space = runCatching { StatFs(Environment.getExternalStorageDirectory().path).availableBytes }.getOrNull() ?: return
        if (space < bytes + 8L * 1024 * 1024) throw UserMessageException("저장 공간이 부족해요. 공간을 확보한 뒤 다시 시도해 주세요.")
    }

    private fun publish(file: File, name: String, mime: String, relativePath: String): Uri {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name); put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath); put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw UserMessageException("갤러리에 저장하지 못했어요. 저장 공간과 권한을 확인해 주세요.")
            try {
                resolver.openOutputStream(uri).use { out -> requireNotNull(out); file.inputStream().use { it.copyTo(out) } }
                check(resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null) == 1)
                return uri
            } catch (e: Exception) { runCatching { resolver.delete(uri, null, null) }; throw e }
        }
        @Suppress("DEPRECATION")
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), relativePath.removePrefix("Pictures/"))
        check(directory.mkdirs() || directory.exists())
        val output = File(directory, name)
        try {
            file.copyTo(output)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name); put(MediaStore.Images.Media.MIME_TYPE, mime)
                @Suppress("DEPRECATION")
                put(MediaStore.Images.Media.DATA, output.path)
            }
            return resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw UserMessageException("갤러리에 사진을 등록하지 못했어요. 다시 시도해 주세요.")
        } catch (e: Exception) { output.delete(); throw e }
    }

    companion object { private const val TimeUnitDays = 24L * 60 * 60 * 1000 }
}
