package studio.moment.camera

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry

/** System key events require window focus; a visible Compose tree alone is not enough. */
internal fun sendCameraKey(compose: ComposeTestRule, scenario: ActivityScenario<MainActivity>, key: Int) {
    compose.waitForIdle()
    compose.waitUntil(15_000) {
        var focused = false
        scenario.onActivity { focused = it.hasWindowFocus() && it.lifecycle.currentState == Lifecycle.State.RESUMED }
        focused
    }
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    instrumentation.sendKeyDownUpSync(key)
    instrumentation.waitForIdleSync()
    compose.waitForIdle()
}
