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
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.color.Looks
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.coroutineContext

data class SavedPhoto(val uri: Uri, val original: Uri?, val model: String)

class PhotoStore(private val context: Context) {
    private val resolver = context.contentResolver
    private val folder get() = File(context.cacheDir, "photos").apply { mkdirs() }

    suspend fun copyImport(uri: Uri): File = withContext(Dispatchers.IO) {
        val file = File.createTempFile("import-", ".image", folder)
        try {
            resolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "사진을 열 수 없어요." }
                file.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var total = 0L
                    while (true) {
                        coroutineContext.ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        require(total <= 100L * 1024 * 1024) { "100MB 이하의 사진을 선택해 주세요." }
                        output.write(buffer, 0, count)
                    }
                }
            }
            file
        } catch (e: Exception) { file.delete(); throw e }
    }

    suspend fun export(source: File, lut: ColorLut, settings: GradeSettings, saveOriginal: Boolean): SavedPhoto = withContext(Dispatchers.IO) {
        val bitmap = decode(source, 16_000_000, mutable = true)
        val output = File.createTempFile("graded-", ".jpg", folder)
        val created = mutableListOf<Uri>()
        try {
            // Chunked processing avoids another full-resolution pixel array.
            val rows = 32
            val pixels = IntArray(bitmap.width * rows)
            for (y in 0 until bitmap.height step rows) {
                coroutineContext.ensureActive()
                val height = minOf(rows, bitmap.height - y)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, y, bitmap.width, height)
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
            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
            val original = if (saveOriginal) {
                val mime = runCatching { BitmapFactory.Options().also { it.inJustDecodeBounds = true; BitmapFactory.decodeFile(source.path, it) }.outMimeType }.getOrNull() ?: "image/jpeg"
                val extension = when (mime) { "image/png" -> "png"; "image/webp" -> "webp"; "image/heif", "image/heic" -> "heic"; else -> "jpg" }
                publish(source, "MOMENT_${stamp}_original.$extension", mime, "Pictures/moment/Originals").also(created::add)
            } else null
            val uri = publish(output, "MOMENT_${stamp}_${settings.lookId}.jpg", "image/jpeg", "Pictures/moment").also(created::add)
            SavedPhoto(uri, original, Looks.find(settings.lookId).model)
        } catch (e: Exception) {
            created.forEach { runCatching { resolver.delete(it, null, null) } }
            throw e
        } finally {
            bitmap.recycle()
            output.delete()
        }
    }

    suspend fun thumbnail(file: File, lut: ColorLut? = null): Bitmap = withContext(Dispatchers.Default) {
        val bitmap = decode(file, 900_000, mutable = true)
        if (lut != null) {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            lut.apply(pixels)
            bitmap.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        }
        bitmap
    }

    suspend fun thumbnail(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            resolver.openInputStream(uri).use { input ->
                requireNotNull(input)
                BitmapFactory.decodeStream(input, null, BitmapFactory.Options().apply { inSampleSize = 4 })
            }
        }.getOrNull()
    }

    private fun decode(source: File, maxPixels: Int, mutable: Boolean): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(source.path, bounds)
        require(bounds.outWidth > 0 && bounds.outHeight > 0) { "이 사진 형식을 열 수 없어요." }
        var sample = 1
        while (bounds.outWidth.toLong() * bounds.outHeight / (sample.toLong() * sample) > maxPixels) sample *= 2
        val bitmap = requireNotNull(BitmapFactory.decodeFile(source.path, BitmapFactory.Options().apply {
            inSampleSize = sample; inMutable = mutable; inPreferredConfig = Bitmap.Config.ARGB_8888
            inPreferredColorSpace = ColorSpace.get(ColorSpace.Named.SRGB)
        })) { "사진을 불러오지 못했어요." }
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
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated !== bitmap) bitmap.recycle()
        if (mutable && !rotated.isMutable) return rotated.copy(Bitmap.Config.ARGB_8888, true).also { rotated.recycle() }
        return rotated
    }

    private fun publish(file: File, name: String, mime: String, relativePath: String): Uri {
        if (Build.VERSION.SDK_INT >= 29) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name); put(MediaStore.Images.Media.MIME_TYPE, mime)
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath); put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)) { "갤러리에 저장할 수 없어요." }
            try {
                resolver.openOutputStream(uri).use { out -> requireNotNull(out); file.inputStream().use { it.copyTo(out) } }
                check(resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null) == 1)
                return uri
            } catch (e: Exception) { resolver.delete(uri, null, null); throw e }
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
                ?: FileProvider.getUriForFile(context, "${context.packageName}.files", file.copyTo(File(folder, name)))
        } catch (e: Exception) { output.delete(); throw e }
    }
}
