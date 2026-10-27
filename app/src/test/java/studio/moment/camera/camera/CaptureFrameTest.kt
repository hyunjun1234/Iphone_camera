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

    private fun assertPixels(expected: PixelFrame, actual: PixelFrame) = assertEquals(expected, actual)
}
