package studio.moment.camera.camera

import org.junit.Assert.*
import org.junit.Test

class CaptureFrameTest {
    @Test fun portraitFrameBecomesTheSameRegionInLandscapeAndUpsideDownPhotos() {
        // The subject is off-centre toward the top, so a wrong rotation changes its crop.
        val frame = CaptureFrame(.1f, .2f, .9f, .6f)
        assertPixels(PixelFrame(80, 30, 80, 240), frame.rotateClockwise(1).pixels(200, 300))
        assertPixels(PixelFrame(30, 80, 240, 80), frame.rotateClockwise(2).pixels(300, 200))
        assertPixels(PixelFrame(40, 30, 80, 240), frame.rotateClockwise(-1).pixels(200, 300))
    }

    @Test fun selfieMirroringMovesTheCropWithTheSubject() {
        val leftSubject = CaptureFrame(.1f, .2f, .4f, .8f)
        assertPixels(PixelFrame(180, 40, 90, 120), leftSubject.mirrorHorizontally().pixels(300, 200))
        assertPixels(leftSubject.pixels(300, 200), leftSubject.mirrorHorizontally().mirrorHorizontally().pixels(300, 200))
    }

    @Test fun roundedEdgesStayInsideSmallAndOddSizedImages() {
        assertPixels(PixelFrame(0, 0, 1, 1), CaptureFrame(0f, 0f, 1f, 1f).pixels(1, 1))
        assertPixels(PixelFrame(100, 198, 1, 1), CaptureFrame(.999f, .999f, 1f, 1f).pixels(101, 199))
        val square = CaptureFrame(0f, .25f, 1f, .75f).pixels(1080, 2160)
        assertEquals(square.width, square.height)
        val roundedSquare = CaptureFrame(0f, .25f, 1f, .75f, 1f).pixels(591, 1180)
        assertEquals(590, roundedSquare.width)
        assertEquals(roundedSquare.width, roundedSquare.height)
    }

    @Test fun invalidFramesAreRejectedBeforeBitmapCropping() {
        for (edges in listOf(listOf(Float.NaN, 0f, 1f, 1f), listOf(-.1f, 0f, 1f, 1f),
            listOf(0f, 0f, 1.1f, 1f), listOf(.5f, 0f, .5f, 1f))) {
            try { CaptureFrame(edges[0], edges[1], edges[2], edges[3]); fail("Invalid frame accepted") }
            catch (_: IllegalArgumentException) { }
        }
    }

    @Test fun eachRatioUsesTheLargestCentredAreaOfTheUprightCameraImage() {
        // 1x must show what the camera sees: 3:4 is the whole image, the others lose one axis only.
        assertFrame(CaptureFrame(0f, 0f, 1f, 1f, .75f), CaptureFrame.centered(.75f, .75f))
        assertFrame(CaptureFrame(.125f, 0f, .875f, 1f, .5625f), CaptureFrame.centered(.5625f, .75f))
        assertFrame(CaptureFrame(0f, .125f, 1f, .875f, 1f), CaptureFrame.centered(1f, .75f))
        // A landscape camera image keeps the full height of an upright frame.
        assertFrame(CaptureFrame(.21875f, 0f, .78125f, 1f, .75f), CaptureFrame.centered(.75f, 4f / 3f))
    }

    @Test fun photosKeepTheFullResolutionOfA12MegapixelCameraImage() {
        assertPixels(PixelFrame(0, 0, 3024, 4032), CaptureFrame.centered(.75f, .75f).pixels(3024, 4032))
        assertPixels(PixelFrame(378, 0, 2268, 4032), CaptureFrame.centered(.5625f, .75f).pixels(3024, 4032))
        assertPixels(PixelFrame(0, 504, 3024, 3024), CaptureFrame.centered(1f, .75f).pixels(3024, 4032))
        // Holding the phone sideways turns the same areas into landscape photos.
        assertPixels(PixelFrame(0, 0, 4032, 3024), CaptureFrame.centered(.75f, .75f).rotateClockwise(1).pixels(4032, 3024))
        assertPixels(PixelFrame(0, 378, 4032, 2268), CaptureFrame.centered(.5625f, .75f).rotateClockwise(1).pixels(4032, 3024))
    }

    @Test fun cameraImageIsPlacedSoTheFrameOnScreenShowsTheSavedArea() {
        // 3:4 fills its frame exactly, so nothing of the camera image is left outside it.
        assertEquals(SceneBounds(0f, 300f, 1080f, 1440f), CaptureFrame.centered(.75f, .75f).sceneAround(0f, 300f, 1080f, 1440f))
        // 9:16 keeps the image height; an eighth of the image continues past each side.
        assertEquals(SceneBounds(-60f, 200f, 1200f, 1600f), CaptureFrame.centered(.5625f, .75f).sceneAround(90f, 200f, 900f, 1600f))
        // 1:1 keeps the image width; an eighth of the image continues above and below.
        assertEquals(SceneBounds(0f, 320f, 1080f, 1440f), CaptureFrame.centered(1f, .75f).sceneAround(0f, 500f, 1080f, 1080f))
    }

    @Test fun touchInsideTheFrameFocusesTheSamePlaceOfTheCameraImage() {
        // 3:4 fills the camera image, so the touch position is already the camera position.
        assertEquals(270f to 1080f, CaptureFrame.centered(.75f, .75f).scenePoint(270f, 1080f, 1080f, 1440f))
        // A 900x1600 9:16 frame sits 150px into a 1200px-wide image: its centre is the image centre.
        assertEquals(150f to 0f, CaptureFrame.centered(.5625f, .75f).scenePoint(0f, 0f, 900f, 1600f))
        assertEquals(600f to 800f, CaptureFrame.centered(.5625f, .75f).scenePoint(450f, 800f, 900f, 1600f))
        // A 1080x1080 1:1 frame sits 180px below the top of a 1440px-tall image.
        assertEquals(1080f to 1260f, CaptureFrame.centered(1f, .75f).scenePoint(1080f, 1080f, 1080f, 1080f))
    }

    @Test fun cameraImageIsUprightUnlessTheSensorAlreadyMatchesTheScreen() {
        assertEquals(3 to 4, cameraImageShape(90))
        assertEquals(3 to 4, cameraImageShape(270))
        assertEquals(4 to 3, cameraImageShape(0))
        assertEquals(4 to 3, cameraImageShape(180))
    }

    private fun assertPixels(expected: PixelFrame, actual: PixelFrame) = assertEquals(expected, actual)
    private fun assertFrame(expected: CaptureFrame, actual: CaptureFrame) {
        assertEquals(expected.left, actual.left, 1e-5f); assertEquals(expected.top, actual.top, 1e-5f)
        assertEquals(expected.right, actual.right, 1e-5f); assertEquals(expected.bottom, actual.bottom, 1e-5f)
        assertEquals(expected.aspectRatio, actual.aspectRatio)
    }
}
