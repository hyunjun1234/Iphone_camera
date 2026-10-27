package studio.moment.camera

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import studio.moment.camera.data.PhotoQuality

@RunWith(AndroidJUnit4::class)
class CameraUsabilityTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val prefs get() = context.getSharedPreferences("moment", Context.MODE_PRIVATE)

    @Before fun prepare() {
        instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.CAMERA)
        prefs.edit().clear().putBoolean("original", false).commit()
    }

    @Test fun selectingTheSameStyleAndReturningToItPreservesAdjustments() {
        ActivityScenario.launch(MainActivity::class.java).use {
            waitForCamera()
            tune()
            setSlider(.42f)
            compose.onNodeWithText("온도", useUnmergedTree = true).performClick()
            setSlider(.35f)
            compose.onNodeWithText("완료").performClick()
            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithContentDescription("XS, 선택됨").performClick()
            assertEquals(.42f, prefs.getFloat("intensity", -1f), .001f)
            assertEquals(.35f, prefs.getFloat("warmth", -1f), .001f)
            compose.onNodeWithContentDescription("11").performClick()
            assertEquals(1f, prefs.getFloat("intensity", -1f), .001f)
            compose.onNodeWithContentDescription("XS").performClick()
            assertEquals(.42f, prefs.getFloat("intensity", -1f), .001f)
            assertEquals(.35f, prefs.getFloat("warmth", -1f), .001f)
        }
    }

    @Test fun stylesCanBeHiddenWithoutResizingTheCameraOrLosingSelection() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            compose.onNodeWithTag("style-picker").assertDoesNotExist()
            compose.onNodeWithText("아이폰 XS 스타일").assertDoesNotExist()
            val frame = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
            val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
            val toolbar = compose.onNodeWithTag("camera-toolbar").fetchSemanticsNode().boundsInRoot
            val tools = compose.onNodeWithTag("camera-tools").fetchSemanticsNode().boundsInRoot
            assertTrue("The camera should fill the available width or height",
                frame.width >= root.width * .94f || frame.height >= (tools.top - toolbar.bottom) * .99f)
            assertEquals(.75f, frame.width / frame.height, .01f)
            compose.onNodeWithTag("shutter").assertIsDisplayed()

            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithTag("style-picker").assertIsDisplayed()
            compose.onNodeWithContentDescription("11").performClick()
            compose.waitUntil { prefs.getString("look", null) == "11" }
            assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)
            compose.onNodeWithContentDescription("스타일 닫기").performClick()
            compose.onNodeWithTag("style-picker").assertDoesNotExist()
            assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)

            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithContentDescription("11, 선택됨").assertIsDisplayed()
            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithTag("style-picker").assertDoesNotExist()
            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithTag("style-picker").assertIsDisplayed()
            compose.waitForIdle()
            sendCameraKey(compose, scenario, android.view.KeyEvent.KEYCODE_BACK)
            compose.onNodeWithTag("style-picker").assertDoesNotExist()
            waitForCamera()
            scenario.recreate()
            waitForCamera()
            compose.onNodeWithTag("style-picker").assertDoesNotExist()
            compose.onNodeWithTag("style-toggle").performClick()
            compose.onNodeWithContentDescription("11, 선택됨").assertIsDisplayed()
        }
    }

    @Test fun comparisonReturnsToStyledViewAfterCaptureAndBackgrounding() {
        prefs.edit().putBoolean("original", true).commit()
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            compose.onNodeWithTag("original-compare").performClick()
            compose.onNodeWithText("색감 보기", substring = true).assertIsDisplayed()
            compose.onNodeWithContentDescription("사진 촬영").performClick()
            compose.waitUntil(30_000) { prefs.getString("latest", null) != null }
            waitForCamera()
            compose.onNodeWithText("원본 비교", substring = true).assertIsDisplayed()
            compose.onNodeWithText("원본과 보정한 사진을 갤러리에 저장했어요.").assertIsDisplayed()
            compose.onNodeWithTag("original-compare").performClick()
            compose.onNodeWithText("색감 보기", substring = true).assertIsDisplayed()
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.moveToState(Lifecycle.State.RESUMED)
            waitForCamera()
            compose.onNodeWithText("원본 비교", substring = true).assertIsDisplayed()
            compose.onNodeWithTag("original-compare").performTouchInput { longClick() }
            compose.onNodeWithText("원본 비교", substring = true).assertIsDisplayed()
        }
    }

    @Test fun zoomControlsStayOutsidePhotoAndCollapseOnlyAfterInteractionEnds() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            // The emulator's virtual-scene rear camera has a fixed 1x lens; its front camera supports zoom.
            if (compose.onAllNodes(hasTestTag("zoom-toggle") and isEnabled()).fetchSemanticsNodes().isEmpty()) {
                compose.onNodeWithContentDescription("카메라 전환").performClick()
                waitForCamera()
            }
            compose.onNodeWithTag("zoom-toggle").assertIsEnabled()
            val frame = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
            compose.onNodeWithTag("zoom-toggle").performClick()
            val slider = compose.onNodeWithTag("zoom-slider").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            assertTrue(slider.top >= frame.bottom)
            val presets = compose.onNodeWithTag("zoom-presets").assertIsDisplayed().fetchSemanticsNode().boundsInRoot
            assertTrue(presets.top >= frame.bottom && presets.bottom <= slider.top)
            assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)
            for (step in listOf(1, 2, 3, 5, 10)) {
                compose.onNodeWithContentDescription("배율 ${step}배").performScrollTo().performClick()
                waitForZoom("${step}배")
                assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)
            }
            compose.onNodeWithTag("zoom-slider").performSemanticsAction(SemanticsActions.SetProgress) { it(2f) }
            waitForZoom("2배")
            compose.onNodeWithTag("zoom-reset").performClick()
            waitForZoom("1배")
            sendCameraKey(compose, scenario, android.view.KeyEvent.KEYCODE_BACK)
            compose.onNodeWithTag("zoom-slider").assertDoesNotExist()
            compose.onNodeWithTag("photo-preview").performTouchInput {
                pinch(center - Offset(40f, 0f), center + Offset(40f, 0f),
                    center - Offset(140f, 0f), center + Offset(140f, 0f), 350)
            }
            compose.onNodeWithTag("zoom-slider").assertIsDisplayed()
            compose.onNodeWithText("이 카메라에서는 요청한 배율을 사용할 수 없어요.").assertDoesNotExist()
            assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)
            compose.onNodeWithTag("zoom-slider").performTouchInput {
                down(centerLeft + Offset(width * .15f, 0f))
                moveTo(center, 200)
            }
            // Holding the slider must not dismiss it, even past the idle timeout.
            android.os.SystemClock.sleep(4_000)
            compose.onNodeWithTag("zoom-slider").assertIsDisplayed().performTouchInput { up() }
            compose.onNodeWithText("이 카메라에서는 요청한 배율을 사용할 수 없어요.").assertDoesNotExist()
            compose.waitUntil(6_000) { compose.onAllNodesWithTag("zoom-slider").fetchSemanticsNodes().isEmpty() }
            compose.onNodeWithTag("zoom-toggle").assertIsDisplayed()
            compose.onNodeWithTag("original-compare").assertIsDisplayed()
            assertEquals(frame, compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot)
        }
    }

    @Test fun photoQualityCanBeChangedWithoutLosingTheCameraAndSurvivesRestart() {
        val saved = mutableListOf<Uri>()
        try { ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            waitForCamera()
            for (quality in PhotoQuality.entries) {
                compose.onNodeWithContentDescription("설정").performClick()
                compose.onNodeWithText("사진 화질").assertIsDisplayed()
                compose.onNodeWithText(quality.label).performClick()
                compose.waitUntil { prefs.getString("photoQuality", null) == quality.name }
                // The settings dialog owns window focus, so dismiss it through its accessibility action.
                compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.Dismiss)).onFirst()
                    .performSemanticsAction(SemanticsActions.Dismiss) { it() }
                waitForCamera()
                val previous = prefs.getString("latest", null)
                compose.onNodeWithContentDescription("사진 촬영").performClick()
                compose.waitUntil(30_000) { prefs.getString("latest", null)?.let { it != previous } == true }
                val uri = Uri.parse(prefs.getString("latest", null)); saved += uri
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
                assertTrue(bounds.outWidth > 0 && bounds.outWidth.toLong() * bounds.outHeight <= quality.maxPixels)
                assertEquals(.75f, bounds.outWidth.toFloat() / bounds.outHeight, .01f)
                waitForCamera()
            }
            scenario.recreate()
            waitForCamera()
            compose.onNodeWithContentDescription("설정").performClick()
            compose.onNodeWithText("높음").assertIsSelected()
        } } finally { saved.forEach { context.contentResolver.delete(it, null, null) } }
    }

    private fun waitForZoom(label: String) {
        compose.waitUntil(5_000) {
            compose.onAllNodes(hasTestTag("zoom-reset") and
                SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, label)).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test fun tuningKeepsPreviewAndComparisonVisibleAndResetsOnlyCurrentItem() {
        prefs.edit().putFloat("intensity", .6f).putFloat("warmth", .4f).putFloat("tone", -.3f).commit()
        ActivityScenario.launch(MainActivity::class.java).use {
            waitForCamera()
            tune()
            compose.onNodeWithTag("photo-preview").assertIsDisplayed()
            compose.onNodeWithTag("original-compare").assertIsDisplayed()
            compose.onNodeWithTag("tune-panel").assertIsDisplayed()
            val preview = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
            val compare = compose.onNodeWithTag("original-compare").fetchSemanticsNode().boundsInRoot
            val panel = compose.onNodeWithTag("tune-panel").fetchSemanticsNode().boundsInRoot
            assertTrue(preview.height > 0)
            assertTrue(preview.bottom <= compare.top)
            assertTrue(compare.bottom <= panel.top)
            assertEquals(.75f, preview.width / preview.height, .01f)
            val root = compose.onRoot().fetchSemanticsNode().boundsInRoot
            assertTrue(preview.top >= root.top)
            assertTrue(panel.bottom <= root.bottom)
            compose.onNodeWithText("온도", useUnmergedTree = true).performClick()
            compose.onNodeWithText("온도 초기화").performClick()
            assertEquals(0f, prefs.getFloat("warmth", -1f), .001f)
            assertEquals(.6f, prefs.getFloat("intensity", -1f), .001f)
            assertEquals(-.3f, prefs.getFloat("tone", -1f), .001f)
            compose.onNodeWithTag("original-compare").performClick()
            compose.onNodeWithText("색감 보기", substring = true).assertIsDisplayed()
            setSlider(.2f)
            compose.onNodeWithText("원본 비교", substring = true).assertIsDisplayed()
            compose.onNodeWithText("완료").performClick()
            waitForCamera()
        }
    }

    @Test fun importedLandscapePhotoKeepsItsFrameAndHasAnExplicitSaveAction() {
        val source = File(context.cacheDir, "usability-landscape.png")
        Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.rgb(160, 100, 60))
            source.outputStream().use { compress(Bitmap.CompressFormat.PNG, 100, it) }
            recycle()
        }
        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                waitForCamera()
                scenario.onActivity { ViewModelProvider(it)[MomentViewModel::class.java].importPhoto(Uri.fromFile(source)) }
                compose.waitUntil(15_000) {
                    compose.onAllNodes(hasContentDescription("편집한 사진 저장") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithText("사진 저장").assertIsDisplayed()
                tune()
                val preview = compose.onNodeWithTag("photo-preview").fetchSemanticsNode().boundsInRoot
                assertEquals(2f, preview.width / preview.height, .01f)
                setSlider(.5f)
                // Back closes tuning, keeping the selected photo open for saving.
                sendCameraKey(compose, scenario, android.view.KeyEvent.KEYCODE_BACK)
                compose.waitUntil(15_000) {
                    compose.onAllNodes(hasContentDescription("편집한 사진 저장") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
                }
                compose.onNodeWithText("사진 저장").performClick()
                compose.waitUntil(15_000) { prefs.getString("latest", null) != null }
                val uri = Uri.parse(prefs.getString("latest", null))
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
                assertEquals(800, bounds.outWidth)
                assertEquals(400, bounds.outHeight)
                compose.onNodeWithText("편집한 사진을 갤러리에 저장했어요.").assertIsDisplayed()
                context.contentResolver.delete(uri, null, null)
            }
        } finally { source.delete() }
    }

    private fun tune() {
        val button = compose.onNodeWithText("색감 조절", substring = true)
        if (!button.isDisplayed()) button.performScrollTo()
        button.performClick()
    }
    private fun setSlider(value: Float) {
        compose.onNodeWithTag("adjustment-slider").performSemanticsAction(SemanticsActions.SetProgress) { it(value) }
        compose.waitForIdle()
    }
    private fun waitForCamera() {
        compose.waitUntil(30_000) {
            compose.onAllNodes(hasContentDescription("사진 촬영") and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
