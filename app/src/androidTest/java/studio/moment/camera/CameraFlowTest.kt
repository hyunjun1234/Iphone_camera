package studio.moment.camera

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.util.Size
import android.view.KeyEvent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.printToString
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.exifinterface.media.ExifInterface
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import studio.moment.camera.data.PhotoQuality
import kotlin.math.abs
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class CameraFlowTest {
    @get:Rule val compose = createEmptyComposeRule()

    @Test fun livePreviewCaptureModelChangeAndSettingsPersistence() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit().clear().commit()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            compose.waitUntil(30_000) {
                compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("스타일 선택").performClick()
            compose.onNodeWithContentDescription("XS, 선택됨").assertIsDisplayed()
            compose.onNodeWithContentDescription("사진 촬영").performClick()
            val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
            compose.waitUntil(30_000) { prefs.getString("latest", null) != null }
            val uri = Uri.parse(prefs.getString("latest", null))
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
            assertTrue(bounds.outWidth > 0 && bounds.outHeight > 0)
            assertEquals(.75f, bounds.outWidth.toFloat() / bounds.outHeight, .02f)
            val exif = context.contentResolver.openInputStream(uri).use { ExifInterface(it!!) }
            assertTrue(exif.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION)!!.contains("xs inspired"))
            compose.onNodeWithContentDescription("11").performClick()
            compose.waitUntil(5_000) { prefs.getString("look", null) == "11" }
            compose.onNodeWithContentDescription("설정").performClick()
            compose.onNodeWithText("원본도 저장").assertIsDisplayed()
            compose.onNodeWithContentDescription("격자").performClick()
            compose.waitUntil(5_000) { !prefs.getBoolean("grid", true) }
            scenario.recreate()
            compose.waitUntil(30_000) {
                compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("11, 선택됨").assertIsDisplayed()
            assertFalse(prefs.getBoolean("grid", true))
            compose.onNodeWithContentDescription("카메라 전환").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 3:4").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 9:16").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 1:1").assertIsDisplayed()
            // The toolbar stays visible while the camera binds the new crop.
            waitForCamera()
            compose.onNodeWithContentDescription("사진 촬영").performClick()
            compose.waitUntil(30_000) { prefs.getString("latest", null) != uri.toString() }
            val square = Uri.parse(prefs.getString("latest", null))
            context.contentResolver.openInputStream(square).use { BitmapFactory.decodeStream(it, null, bounds) }
            assertTrue(bounds.outWidth > 0)
            assertEquals(bounds.outWidth, bounds.outHeight)
        }
    }

    @Test fun cameraImageIsShownJustLargeEnoughToFillEachFrame() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        context.getSharedPreferences("moment", Context.MODE_PRIVATE).edit().clear().commit()
        ActivityScenario.launch(MainActivity::class.java).use {
            waitForCamera()
            for ((label, ratio) in listOf("3:4" to .75f, "9:16" to .5625f, "1:1" to 1f)) {
                compose.onNodeWithContentDescription("사진 비율 $label").assertIsDisplayed()
                waitForCamera()
                val frame = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
                val shutter = compose.onNodeWithTag("shutter").fetchSemanticsNode().boundsInRoot
                assertEquals(ratio, frame.width / frame.height, .005f)
                // An upright phone sees the camera image as 3:4. Enlarging it past the frame would zoom in at 1x.
                val scene = compose.onNodeWithTag("camera-scene").fetchSemanticsNode().let { Rect(it.positionInRoot, it.size.toSize()) }
                assertEquals("$label camera image shape", .75f, scene.width / scene.height, .005f)
                assertEquals("$label camera image is centred on the frame", frame.center.x, scene.center.x, 1f)
                assertEquals("$label camera image is centred on the frame", frame.center.y, scene.center.y, 1f)
                assertTrue("$label camera image $scene must cover the frame $frame",
                    scene.width >= frame.width - 1f && scene.height >= frame.height - 1f)
                assertTrue("$label camera image $scene must not be larger than the frame $frame needs",
                    minOf(scene.width - frame.width, scene.height - frame.height) <= 1.5f)
                for (tag in listOf("camera-tools", "zoom-toggle", "original-compare")) {
                    val control = compose.onNodeWithTag(tag).assertIsDisplayed().fetchSemanticsNode().boundsInRoot
                    assertTrue("$tag must stay below the $label photo", control.top >= frame.bottom)
                    assertTrue("$tag must stay above the shutter", control.bottom <= shutter.top)
                }
                compose.onNodeWithContentDescription("사진 비율 $label").performClick()
            }
        }
    }

    @Test fun photoAtOneTimesKeepsTheWidestViewOfTheCameraInEveryRatio() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
        prefs.edit().clear().putBoolean("original", false).commit()
        val camera = standardRearCameraImage(context)
        val saved = mutableListOf<Uri>()
        try { ActivityScenario.launch(MainActivity::class.java).use {
            waitForCamera()
            for ((label, ratio) in listOf("3:4" to .75f, "9:16" to .5625f, "1:1" to 1f)) {
                compose.onNodeWithContentDescription("사진 비율 $label").assertIsDisplayed()
                waitForCamera()
                val previous = prefs.getString("latest", null)
                compose.onNodeWithContentDescription("사진 촬영").assertIsEnabled().performClick()
                try {
                    compose.waitUntil(30_000) { prefs.getString("latest", null)?.let { it != previous } == true }
                } catch (failure: androidx.compose.ui.test.ComposeTimeoutException) {
                    throw AssertionError("Photo was not saved for $label.\n${compose.onRoot().printToString()}", failure)
                }
                val uri = Uri.parse(prefs.getString("latest", null))
                saved += uri
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
                assertTrue(bounds.outWidth > 0)
                assertEquals("Saved crop for $label", ratio, bounds.outWidth.toFloat() / bounds.outHeight, .005f)
                // The test phone is upright: the camera's long side is the photo's height.
                val (width, height) = when (label) {
                    "3:4" -> camera.height to camera.width
                    "9:16" -> (camera.width * 9f / 16f).roundToInt() to camera.width
                    else -> camera.height to camera.height
                }
                if (width.toLong() * height <= PhotoQuality.STANDARD.maxPixels) {
                    assertEquals("$label photo width at 1x from a ${camera.width}x${camera.height} camera image",
                        width.toFloat(), bounds.outWidth.toFloat(), 1f)
                    assertEquals("$label photo height at 1x from a ${camera.width}x${camera.height} camera image",
                        height.toFloat(), bounds.outHeight.toFloat(), 1f)
                } else assertTrue("$label photo must use the pixels the quality setting allows",
                    bounds.outWidth.toLong() * bounds.outHeight > PhotoQuality.STANDARD.maxPixels * .97)
                waitForCamera()
                compose.onNodeWithContentDescription("사진 비율 $label").performClick()
            }
        } } finally { saved.forEach { context.contentResolver.delete(it, null, null) } }
    }

    /** The rear camera's largest 4:3 picture up to 4032x3024, which the standard photo quality requests. */
    private fun standardRearCameraImage(context: Context): Size {
        val manager = context.getSystemService(CameraManager::class.java)
        val id = manager.cameraIdList.first {
            manager.getCameraCharacteristics(it).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK
        }
        val sizes = manager.getCameraCharacteristics(id).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(ImageFormat.JPEG).filter { abs(it.width.toFloat() / it.height - 4f / 3f) < .02f }
        return sizes.filter { it.width.toLong() * it.height <= 4032L * 3024 }.maxByOrNull { it.width.toLong() * it.height }
            ?: sizes.minBy { it.width.toLong() * it.height }
    }

    @Test fun volumeShutterAndTimerCancellationSurviveBackgrounding() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
        prefs.edit().clear().putBoolean("original", false).putInt("timer", 10).commit()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            sendCameraKey(compose, scenario, KeyEvent.KEYCODE_VOLUME_DOWN)
            compose.onNodeWithText("탭하여 취소").assertIsDisplayed()
            sendCameraKey(compose, scenario, KeyEvent.KEYCODE_VOLUME_DOWN)
            waitForCamera()
            assertNull(prefs.getString("latest", null))
            sendCameraKey(compose, scenario, KeyEvent.KEYCODE_VOLUME_UP)
            compose.onNodeWithText("탭하여 취소").assertIsDisplayed()
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)
            waitForCamera()
            compose.onNodeWithText("탭하여 취소").assertDoesNotExist()
            assertNull(prefs.getString("latest", null))
            // 10 seconds cycles back to off. Capture with the physical volume key.
            compose.onNodeWithContentDescription("타이머 10초").performClick()
            sendCameraKey(compose, scenario, KeyEvent.KEYCODE_VOLUME_UP)
            compose.waitUntil(30_000) { prefs.getString("latest", null) != null }
            val uri = Uri.parse(prefs.getString("latest", null))
            assertNotNull(context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) })
            context.contentResolver.delete(uri, null, null)
        }
    }
    private fun waitForCamera() {
        compose.waitUntil(30_000) {
            compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
