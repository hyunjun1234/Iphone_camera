package studio.moment.camera.camera

import org.junit.Assert.*
import org.junit.Test

class ZoomStepsTest {
    @Test fun cameraWith100xKeepsItsFullRange() {
        assertEquals(listOf(.6f, 1f, 2f, 3f, 5f, 10f, 30f, 100f), zoomSteps(.6f, 100f))
    }
    @Test fun shortcutsNeverRequestUnsupportedZoom() {
        assertEquals(listOf(1f, 2f, 3f, 5f, 8f), zoomSteps(1f, 8f))
        assertEquals(listOf(1f, 2f, 3f, 5f, 10f, 12.9f), zoomSteps(1f, 12.9f))
        assertEquals(listOf(1f), zoomSteps(1f, 1f))
    }
}
