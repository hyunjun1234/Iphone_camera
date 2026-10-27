package studio.moment.camera

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.compose.ui.test.assertIsDisplayed
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

    @Test fun cameraSceneCoversControlsWhileEachPhotoUsesOnlyItsSelectedFrame() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
        prefs.edit().clear().putBoolean("original", false).commit()
        ActivityScenario.launch(MainActivity::class.java).use {
            waitForCamera()
            val scene = compose.onNodeWithTag("camera-scene").fetchSemanticsNode().boundsInRoot
            val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
            assertEquals(root, scene)
            for ((label, ratio) in listOf("3:4" to .75f, "9:16" to .5625f, "1:1" to 1f)) {
                compose.onNodeWithContentDescription("사진 비율 $label").assertIsDisplayed()
                val frame = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
                val shutter = compose.onNodeWithTag("shutter").fetchSemanticsNode().boundsInRoot
                assertTrue(scene.contains(frame.center))
                assertTrue(scene.bottom >= shutter.bottom)
                assertTrue(frame.top > scene.top && frame.bottom < scene.bottom)
                assertEquals(ratio, frame.width / frame.height, .005f)
                assertEquals(scene, compose.onNodeWithTag("camera-scene").fetchSemanticsNode().boundsInRoot)
                val previous = prefs.getString("latest", null)
                compose.onNodeWithContentDescription("사진 촬영").performClick()
                compose.waitUntil(30_000) { prefs.getString("latest", null) != previous }
                val uri = Uri.parse(prefs.getString("latest", null))
                try {
                    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                    context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
                    assertTrue(bounds.outWidth > 0)
                    assertEquals("Saved crop for $label", ratio, bounds.outWidth.toFloat() / bounds.outHeight, .005f)
                } finally { context.contentResolver.delete(uri, null, null) }
                waitForCamera()
                compose.onNodeWithContentDescription("사진 비율 $label").performClick()
            }
        }
    }

    @Test fun volumeShutterAndTimerCancellationSurviveBackgrounding() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
        prefs.edit().clear().putBoolean("original", false).putInt("timer", 10).commit()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN)
            compose.onNodeWithText("탭하여 취소").assertIsDisplayed()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN)
            waitForCamera()
            assertNull(prefs.getString("latest", null))
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP)
            compose.onNodeWithText("탭하여 취소").assertIsDisplayed()
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)
            waitForCamera()
            compose.onNodeWithText("탭하여 취소").assertDoesNotExist()
            assertNull(prefs.getString("latest", null))
            // 10 seconds cycles back to off. Capture with the physical volume key.
            compose.onNodeWithContentDescription("타이머 10초").performClick()
            instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_UP)
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
