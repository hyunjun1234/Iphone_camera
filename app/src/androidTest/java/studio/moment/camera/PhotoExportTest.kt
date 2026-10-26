package studio.moment.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.data.PhotoStore
import java.io.File
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class PhotoExportTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun exportNormalizesExifRotationPreservesOriginalAndPublishesBothFiles() = runBlocking {
        val source = File.createTempFile("orientation-test", ".jpg", context.cacheDir)
        val bitmap = Bitmap.createBitmap(120, 80, Bitmap.Config.ARGB_8888)
        for (y in 0 until 80) for (x in 0 until 120) {
            bitmap.setPixel(x, y, if (y < 40) { if (x < 60) 0xFFFF0000.toInt() else 0xFF00FF00.toInt() }
                else { if (x < 60) 0xFF0000FF.toInt() else 0xFFFFFF00.toInt() })
        }
        source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        bitmap.recycle()
        ExifInterface(source).apply {
            setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90.toString())
            setAttribute(ExifInterface.TAG_MODEL, "Test camera")
            saveAttributes()
        }
        val originalBytes = source.readBytes()
        val result = PhotoStore(context).export(source, ColorLut.identity, GradeSettings(intensity = 0f), true)
        try {
            val saved = context.contentResolver.openInputStream(result.uri).use { BitmapFactory.decodeStream(it) }!!
            assertEquals(80, saved.width); assertEquals(120, saved.height)
            assertColorClose(0xFF0000FF.toInt(), saved.getPixel(20, 20))
            assertColorClose(0xFFFF0000.toInt(), saved.getPixel(60, 20))
            saved.recycle()
            val exif = context.contentResolver.openInputStream(result.uri).use { ExifInterface(it!!) }
            assertEquals(ExifInterface.ORIENTATION_NORMAL, exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0))
            assertEquals("Test camera", exif.getAttribute(ExifInterface.TAG_MODEL))
            assertTrue(exif.getAttribute(ExifInterface.TAG_SOFTWARE)!!.contains("moment"))
            assertArrayEquals(originalBytes, context.contentResolver.openInputStream(result.original!!).use { it!!.readBytes() })
            assertArrayEquals(originalBytes, source.readBytes())
        } finally {
            context.contentResolver.delete(result.uri, null, null)
            result.original?.let { context.contentResolver.delete(it, null, null) }
            source.delete()
        }
    }

    @Test fun importedPhotoSavesStyleToNewFileAndLeavesSourceUntouched() = runBlocking {
        val source = File.createTempFile("style-test", ".png", context.cacheDir)
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF808080.toInt()) }
        source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }; bitmap.recycle()
        val sourceBytes = source.readBytes()
        val settings = GradeSettings("6s")
        val lut = ColorLut.create(settings)
        val result = PhotoStore(context).export(source, lut, settings, false)
        try {
            assertNull(result.original)
            val output = context.contentResolver.openInputStream(result.uri).use { BitmapFactory.decodeStream(it) }!!
            assertColorClose(lut.sample(0xFF808080.toInt()), output.getPixel(32, 32))
            assertNotEquals(0xFF808080.toInt(), output.getPixel(32, 32))
            output.recycle()
            assertArrayEquals(sourceBytes, source.readBytes())
        } finally { context.contentResolver.delete(result.uri, null, null); source.delete() }
    }

    private fun assertColorClose(expected: Int, actual: Int) {
        for (shift in listOf(0, 8, 16)) assertTrue("JPEG color mismatch: $expected / $actual", abs(((expected ushr shift) and 255) - ((actual ushr shift) and 255)) <= 8)
    }
}
