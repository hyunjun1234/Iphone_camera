package studio.moment.camera.color

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs
import kotlin.random.Random

class ColorLutTest {
    @Test fun zeroIntensityPreservesSourceIncludingAlpha() {
        val random = Random(17)
        val lut = ColorLut.create(GradeSettings(lookId = "6s", intensity = 0f, warmth = 1f, tone = -1f))
        repeat(10_000) {
            val input = random.nextInt()
            val output = lut.sample(input)
            assertEquals(input ushr 24, output ushr 24)
            for (shift in listOf(0, 8, 16)) assertTrue(abs(((input ushr shift) and 255) - ((output ushr shift) and 255)) <= 1)
        }
    }

    @Test fun lutBoundaryAndCubeCornersNeverOverflow() {
        for (look in Looks.all) {
            val lut = ColorLut.create(GradeSettings(look.id))
            for (r in listOf(0, 1, 254, 255)) for (g in listOf(0, 1, 254, 255)) for (b in listOf(0, 1, 254, 255)) {
                val output = lut.sample(0xFF000000.toInt() or (r shl 16) or (g shl 8) or b)
                assertEquals(255, output ushr 24)
            }
        }
    }

    @Test fun allProfilesPreserveMonotonicNeutralRamp() {
        for (look in Looks.all) {
            val lut = ColorLut.create(GradeSettings(look.id))
            var last = intArrayOf(0, 0, 0)
            for (v in 0..255) {
                val out = lut.sample(0xFF000000.toInt() or (v shl 16) or (v shl 8) or v)
                val channels = intArrayOf((out ushr 16) and 255, (out ushr 8) and 255, out and 255)
                channels.forEachIndexed { index, channel -> assertTrue("${look.id}: ramp reverses at $v", channel >= last[index]) }
                last = channels
            }
        }
    }

    @Test fun textureLayoutMatchesCpuSamplingAtEveryGridNode() {
        val lut = ColorLut.create(GradeSettings("xs"))
        val bytes = lut.rgbaBytes()
        assertEquals(33 * 33 * 33 * 4, bytes.size)
        for (i in lut.colors.indices) {
            assertEquals((lut.colors[i] ushr 16) and 255, bytes[i * 4].toInt() and 255)
            assertEquals((lut.colors[i] ushr 8) and 255, bytes[i * 4 + 1].toInt() and 255)
            assertEquals(lut.colors[i] and 255, bytes[i * 4 + 2].toInt() and 255)
            assertEquals(255, bytes[i * 4 + 3].toInt() and 255)
        }
    }

    @Test fun unknownProfileUsesDefaultAndStrengthIsClamped() {
        assertArrayEquals(ColorLut.create(GradeSettings()).colors, ColorLut.create(GradeSettings("missing")).colors)
        assertArrayEquals(ColorLut.identity.colors, ColorLut.create(GradeSettings(intensity = -5f)).colors)
        assertArrayEquals(ColorLut.create(GradeSettings()).colors, ColorLut.create(GradeSettings(intensity = 10f)).colors)
    }

    @Test fun chunkedProcessingHonorsCountAndPreservesTrailingData() {
        val lut = ColorLut.create(GradeSettings())
        val pixels = intArrayOf(0xFFABCDEF.toInt(), 0xFF555555.toInt(), 12345)
        val expected = pixels.take(2).map(lut::sample)
        lut.apply(pixels, 2)
        assertEquals(expected[0], pixels[0]); assertEquals(expected[1], pixels[1]); assertEquals(12345, pixels[2])
    }

    @Test fun profilesProduceDistinctResultsAndWarmthMovesNeutralColor() {
        val grey = 0xFF808080.toInt()
        assertEquals(Looks.all.size, Looks.all.map { ColorLut.create(GradeSettings(it.id)).sample(grey) }.toSet().size)
        val cool = ColorLut.create(GradeSettings(warmth = -1f)).sample(grey)
        val warm = ColorLut.create(GradeSettings(warmth = 1f)).sample(grey)
        assertTrue(((warm ushr 16) and 255) > ((cool ushr 16) and 255))
        assertTrue((warm and 255) < (cool and 255))
    }
}
