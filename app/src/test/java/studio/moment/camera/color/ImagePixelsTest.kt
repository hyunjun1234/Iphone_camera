package studio.moment.camera.color

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ImagePixelsTest {
    @Test fun transparentAndSemitransparentPhotosHavePredictableJpegBackground() {
        val pixels = intArrayOf(0x00000000, 0x80FF0000.toInt(), 0xFF336699.toInt(), 7)
        compositeOnWhite(pixels, 3)
        assertArrayEquals(intArrayOf(0xFFFFFFFF.toInt(), 0xFFFF7F7F.toInt(), 0xFF336699.toInt(), 7), pixels)
    }
}
