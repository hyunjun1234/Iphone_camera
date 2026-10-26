package studio.moment.camera.camera

import org.junit.Assert.assertEquals
import org.junit.Test

class CaptureOrientationTest {
    @Test fun allFourPhysicalDirectionsMapToCameraRotation() {
        assertEquals(0, CaptureOrientation.fromSensor(0))
        assertEquals(3, CaptureOrientation.fromSensor(90))
        assertEquals(2, CaptureOrientation.fromSensor(180))
        assertEquals(1, CaptureOrientation.fromSensor(270))
        assertEquals(0, CaptureOrientation.fromSensor(359))
    }

    @Test fun unknownAndBoundaryReadingsDoNotFlipCapturedOrientation() {
        assertEquals(2, CaptureOrientation.fromSensor(-1, 2))
        assertEquals(0, CaptureOrientation.fromSensor(44, 0))
        assertEquals(3, CaptureOrientation.fromSensor(46, 3))
        assertEquals(3, CaptureOrientation.fromSensor(55, 0))
        assertEquals(1, CaptureOrientation.fromSensor(360, 1))
    }
}
