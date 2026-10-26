package studio.moment.camera

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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
            compose.onNodeWithContentDescription("iPhone XS, 기억 속의 온도, 선택됨").assertIsDisplayed()
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
            compose.onNodeWithContentDescription("iPhone 11, 자연스러운 순간").performClick()
            compose.waitUntil(5_000) { prefs.getString("look", null) == "11" }
            compose.onNodeWithContentDescription("설정").performClick()
            compose.onNodeWithText("원본도 저장").assertIsDisplayed()
            compose.onNodeWithContentDescription("격자").performClick()
            compose.waitUntil(5_000) { !prefs.getBoolean("grid", true) }
            scenario.recreate()
            compose.waitUntil(30_000) {
                compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
            }
            compose.onNodeWithContentDescription("iPhone 11, 자연스러운 순간, 선택됨").assertIsDisplayed()
            assertFalse(prefs.getBoolean("grid", true))
            compose.onNodeWithContentDescription("카메라 전환").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 3:4").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 9:16").performClick()
            waitForCamera()
            compose.onNodeWithContentDescription("사진 비율 1:1").assertIsDisplayed()
            compose.onNodeWithContentDescription("사진 촬영").performClick()
            compose.waitUntil(30_000) { prefs.getString("latest", null) != uri.toString() }
            val square = Uri.parse(prefs.getString("latest", null))
            context.contentResolver.openInputStream(square).use { BitmapFactory.decodeStream(it, null, bounds) }
            assertTrue(bounds.outWidth > 0)
            assertEquals(bounds.outWidth, bounds.outHeight)
        }
    }
    private fun waitForCamera() {
        compose.waitUntil(30_000) {
            compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
