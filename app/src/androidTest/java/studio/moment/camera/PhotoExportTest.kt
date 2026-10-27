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
import studio.moment.camera.camera.CaptureFrame
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.data.PhotoStore
import studio.moment.camera.data.UserMessageException
import java.io.File
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class PhotoExportTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun cameraFrameExcludesSurroundAndKeepsTheSameOriginalCropAfterExifRotation() = runBlocking {
        val source = File.createTempFile("frame-test", ".jpg", context.cacheDir)
        val bitmap = Bitmap.createBitmap(300, 200, Bitmap.Config.ARGB_8888)
        // Red surroundings and an asymmetric two-colour subject in the selected rectangle.
        for (y in 0 until 200) for (x in 0 until 300) bitmap.setPixel(x, y,
            if (x in 30 until 150 && y in 40 until 160) {
                if (x < 90) 0xFF00FF00.toInt() else 0xFF0000FF.toInt()
            } else 0xFFFF0000.toInt())
        val store = PhotoStore(context)
        try {
            for (orientation in listOf(1, 2, 3, 6, 8)) {
                source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                ExifInterface(source).apply {
                    setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString()); saveAttributes()
                }
                val sourceBytes = source.readBytes()
                val frame = when (orientation) {
                    2 -> CaptureFrame(.5f, .2f, .9f, .8f)
                    3 -> CaptureFrame(.5f, .2f, .9f, .8f)
                    6 -> CaptureFrame(.2f, .1f, .8f, .5f)
                    8 -> CaptureFrame(.2f, .5f, .8f, .9f)
                    else -> CaptureFrame(.1f, .2f, .5f, .8f)
                }
                val settings = GradeSettings("6s")
                val lut = ColorLut.create(settings)
                val result = store.export(source, lut, settings, true, frame)
                try {
                    for ((uri, graded) in listOf(result.uri to true, result.original!! to false)) {
                        val saved = context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }!!
                        try {
                            assertEquals(120, saved.width); assertEquals(120, saved.height)
                            val first = if (orientation in listOf(2, 3, 8)) 0xFF0000FF.toInt() else 0xFF00FF00.toInt()
                            val last = if (orientation in listOf(2, 3, 8)) 0xFF00FF00.toInt() else 0xFF0000FF.toInt()
                            assertColorClose(if (graded) lut.sample(first) else first, saved.getPixel(20, 20))
                            assertColorClose(if (graded) lut.sample(last) else last, saved.getPixel(100, 100))
                        } finally { saved.recycle() }
                        val exif = context.contentResolver.openInputStream(uri).use { ExifInterface(it!!) }
                        assertEquals(1, exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0))
                    }
                    assertArrayEquals(sourceBytes, source.readBytes())
                } finally {
                    context.contentResolver.delete(result.uri, null, null)
                    context.contentResolver.delete(result.original!!, null, null)
                }
            }
        } finally { bitmap.recycle(); source.delete() }
    }

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
            setLatLong(37.5, 127.0)
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
            assertNull(exif.latLong)
            assertTrue(exif.getAttribute(ExifInterface.TAG_SOFTWARE)!!.contains("moment"))
            assertArrayEquals(originalBytes, context.contentResolver.openInputStream(result.original!!).use { it!!.readBytes() })
            assertArrayEquals(originalBytes, source.readBytes())
        } finally {
            context.contentResolver.delete(result.uri, null, null)
            result.original?.let { context.contentResolver.delete(it, null, null) }
            source.delete()
        }
    }

    @Test fun allExifRotationsAndReflectionsProduceUprightPixels() = runBlocking {
        val store = PhotoStore(context)
        val colors = intArrayOf(0xFFFF0000.toInt(), 0xFF00FF00.toInt(), 0xFF0000FF.toInt(), 0xFFFFFF00.toInt())
        val expectedTopLeft = intArrayOf(0, 1, 3, 2, 0, 2, 3, 1)
        val bitmap = Bitmap.createBitmap(120, 80, Bitmap.Config.ARGB_8888)
        for (y in 0 until 80) for (x in 0 until 120) bitmap.setPixel(x, y, colors[(if (y < 40) 0 else 2) + if (x < 60) 0 else 1])
        val source = File.createTempFile("exif-test", ".jpg", context.cacheDir)
        try {
            for (orientation in 1..8) {
                source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
                ExifInterface(source).apply { setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString()); saveAttributes() }
                val output = store.thumbnail(source)
                try {
                    assertEquals(if (orientation < 5) 120 else 80, output.width)
                    assertEquals(if (orientation < 5) 80 else 120, output.height)
                    assertColorClose(colors[expectedTopLeft[orientation - 1]], output.getPixel(20, 20))
                } finally { output.recycle() }
            }
        } finally { bitmap.recycle(); source.delete() }
    }

    @Test fun transparentImportUsesWhiteBackgroundAndInvalidInputIsRejected() = runBlocking {
        val source = File.createTempFile("alpha-test", ".png", context.cacheDir)
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }; bitmap.recycle()
        val store = PhotoStore(context)
        try {
            val result = store.export(source, ColorLut.identity, GradeSettings(intensity = 0f), false)
            try {
                val saved = context.contentResolver.openInputStream(result.uri).use { BitmapFactory.decodeStream(it) }!!
                assertColorClose(0xFFFFFFFF.toInt(), saved.getPixel(30, 30)); saved.recycle()
            } finally { context.contentResolver.delete(result.uri, null, null) }
            source.writeText("not an image")
            try {
                store.export(source, ColorLut.identity, GradeSettings(), false)
                fail("Invalid image must not be published")
            } catch (e: UserMessageException) { assertTrue(e.message!!.contains("사진")) }
        } finally { source.delete() }
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
