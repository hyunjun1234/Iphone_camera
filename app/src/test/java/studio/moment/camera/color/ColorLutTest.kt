package studio.moment.camera.color

import org.junit.Assert.*
import org.junit.Test
import kotlin.math.abs
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.sqrt
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

    @Test fun everyStyleChangesAColourChartClearly() {
        for (look in Looks.all) {
            val styled = styledChart(look.id)
            val change = chart.indices.map { distance(chart[it], styled[it]) }.average()
            assertTrue("${look.id} changes the chart by only %.1f".format(change), change >= 7.0)
        }
    }

    @Test fun noTwoStylesRenderTheChartAlike() {
        val styled = Looks.all.associate { it.id to styledChart(it.id) }
        for (a in Looks.all) for (b in Looks.all) if (a.id < b.id) {
            val apart = chart.indices.map { distance(styled.getValue(a.id)[it], styled.getValue(b.id)[it]) }.average()
            assertTrue("${a.id} and ${b.id} are only %.1f apart".format(apart), apart >= 1.8)
        }
    }

    /** Every measured iPhone renders grey yellower than the Galaxy average; XS and 6s are the warmest two. */
    @Test fun everyStyleTurnsGreyYellowerAndXsAnd6sAreTheWarmest() {
        val grey = Looks.all.associate { it.id to ColorLut.create(GradeSettings(it.id)).sample(0xFF808080.toInt()) }
        for ((id, color) in grey) assertTrue("$id leaves blue at or above green", blue(color) < green(color))
        val warmest = grey.entries.sortedByDescending { red(it.value) - blue(it.value) }.take(2).map { it.key }.toSet()
        assertEquals(setOf("xs", "6s"), warmest)
    }

    /** Mid grey on the charts: 6s is the darkest, 7 follows, and every later model is brighter, 16 the most. */
    @Test fun olderStylesDarkenMidGreyAndNewerOnesBrightenIt() {
        val brightness = Looks.all.associate { it.id to green(ColorLut.create(GradeSettings(it.id)).sample(0xFF808080.toInt())) }
        assertTrue(brightness.getValue("6s") < brightness.getValue("7"))
        assertTrue(brightness.getValue("7") < 128)
        for (id in listOf("8", "x", "xs", "11", "13", "15pro")) {
            assertTrue("$id should brighten mid grey", brightness.getValue(id) > 128)
            assertTrue("16 should be brighter than $id", brightness.getValue("16") > brightness.getValue(id))
        }
    }

    @Test fun strongestContrastKeepsDetailFromDeepShadowToBrightHighlight() {
        for (look in Looks.all) {
            val lut = ColorLut.create(GradeSettings(look.id, tone = 1f))
            var last = lut.sample(0xFF101010.toInt())
            for (v in 24..224 step 8) {
                val out = lut.sample(0xFF000000.toInt() or (v shl 16) or (v shl 8) or v)
                for (channel in listOf(::red, ::green, ::blue)) {
                    assertTrue("${look.id}: no detail between ${v - 8} and $v", channel(out) > channel(last))
                }
                last = out
            }
        }
    }

    @Test fun profilesProduceDistinctResultsAndWarmthMovesNeutralColor() {
        val grey = 0xFF808080.toInt()
        assertEquals(Looks.all.size, Looks.all.map { ColorLut.create(GradeSettings(it.id)).sample(grey) }.toSet().size)
        val cool = ColorLut.create(GradeSettings(warmth = -1f)).sample(grey)
        val warm = ColorLut.create(GradeSettings(warmth = 1f)).sample(grey)
        assertTrue(((warm ushr 16) and 255) > ((cool ushr 16) and 255))
        assertTrue((warm and 255) < (cool and 255))
    }

    /** The 24 ColorChecker patches in sRGB: skin, sky, foliage, primaries and six greys. */
    private val chart = intArrayOf(
        0x735244, 0xC29682, 0x627A9D, 0x576C43, 0x8580B1, 0x67BDAA, 0xD67E2C, 0x505BA6, 0xC15A63, 0x5E3C6C, 0x9DBC40, 0xE0A32E,
        0x383D96, 0x469449, 0xAF363C, 0xE7C71F, 0xBB5695, 0x0885A1, 0xF3F3F2, 0xC8C8C8, 0xA0A0A0, 0x7A7A79, 0x555555, 0x343434,
    ).map { it or 0xFF000000.toInt() }

    private fun styledChart(lookId: String) = ColorLut.create(GradeSettings(lookId)).let { lut -> chart.map(lut::sample) }

    private fun red(color: Int) = (color ushr 16) and 255
    private fun green(color: Int) = (color ushr 8) and 255
    private fun blue(color: Int) = color and 255

    /** CIE76 colour difference; about 2 is just visible side by side. */
    private fun distance(a: Int, b: Int): Double {
        val (l1, a1, b1) = lab(a); val (l2, a2, b2) = lab(b)
        return sqrt((l1 - l2).pow(2) + (a1 - a2).pow(2) + (b1 - b2).pow(2))
    }

    private fun lab(color: Int): Triple<Double, Double, Double> {
        fun linear(channel: Int) = (channel / 255.0).let { if (it <= .04045) it / 12.92 else ((it + .055) / 1.055).pow(2.4) }
        fun f(t: Double) = if (t > 216.0 / 24389) cbrt(t) else (24389.0 / 27 * t + 16) / 116
        val r = linear(red(color)); val g = linear(green(color)); val b = linear(blue(color))
        val x = f((.4124 * r + .3576 * g + .1805 * b) / .9505)
        val y = f(.2126 * r + .7152 * g + .0722 * b)
        val z = f((.0193 * r + .1192 * g + .9505 * b) / 1.089)
        return Triple(116 * y - 16, 500 * (x - y), 200 * (y - z))
    }
}
