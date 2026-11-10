package studio.moment.camera.color

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * The change that turns an average Galaxy photo of a colour chart into each iPhone's, in SDR sRGB.
 * The numbers come from scripts/color/derive_looks.py (chart photos, not Apple data); docs/COLOR_EVIDENCE.md
 * says what is measured and what is a choice.
 */
data class Look(
    val id: String,
    val model: String,
    val year: String,
    val title: String,
    val description: String,
    val swatch: Long,
    val warmth: Float,
    val tint: Float,
    val saturation: Float,
    val contrast: Float,
    val fade: Float,
    val highlight: Float,
    val gamma: Float = 1f,
)

object Looks {
    val all = listOf(
        Look("6s", "6s", "2015", "어둡고 따뜻한 색", "중간 밝기와 채도를 낮추고 노란 기를 더해요. 검은색은 살짝 들떠 보여요.", 0xFFE2C6A4, .033f, -.013f, .842f, .962f, .050f, -.020f, 1.322f),
        Look("7", "7", "2016", "깊은 명암", "어두운 곳은 더 어둡게, 밝은 곳은 더 밝게 하고 색은 조금 덜어내요.", 0xFFD9BD7F, .011f, -.010f, .691f, 1.266f, .022f, -.020f, 1.122f),
        Look("8", "8", "2017", "밝고 진한 색", "중간 밝기와 대비를 올리고 색을 진하게 해요.", 0xFFD4BEA8, .012f, -.010f, 1.154f, 1.205f, .000f, -.020f, .894f),
        Look("x", "X", "2017", "밝고 또렷한 명암", "중간 밝기와 대비를 올리고 노란 기를 조금 더해요.", 0xFFC0B4A0, .020f, -.013f, 1.008f, 1.250f, .000f, -.020f, .876f),
        Look("xs", "XS", "2018", "따뜻하고 화사한 색", "노란 기를 가장 많이 더하고 밝기와 채도를 올려요.", 0xFFD9CFAD, .044f, -.017f, 1.245f, 1.216f, .020f, .040f, .905f),
        Look("11", "11", "2019", "밝고 부드러운 명암", "어두운 곳과 중간 밝기를 크게 올려 부드럽게 표현해요.", 0xFFB7C5AD, .005f, -.013f, 1.351f, 1.031f, .000f, .040f, .790f),
        Look("13", "13", "2021", "가장 진한 색", "채도를 가장 많이 올리고 밝기와 대비도 높여요.", 0xFFCFD29A, .008f, -.015f, 1.366f, 1.305f, .000f, .005f, .844f),
        Look("15pro", "15 Pro", "2023", "밝고 선명한 색", "밝기와 대비를 올리고 색을 진하게 해요. 색 치우침은 가장 적어요.", 0xFFC4BCAF, .009f, -.008f, 1.342f, 1.310f, .011f, .016f, .814f),
        Look("16", "16", "2024", "가장 밝은 색", "중간 밝기를 가장 많이 올리고 검은색을 살짝 띄워요.", 0xFFC6C7B5, .011f, -.011f, 1.315f, 1.350f, .050f, .031f, .802f),
    )
    val default = all.first { it.id == "xs" }
    fun find(id: String) = all.find { it.id == id } ?: default
}

data class GradeSettings(
    val lookId: String = "xs",
    val intensity: Float = 1f,
    val warmth: Float = 0f,
    val tone: Float = 0f,
)

/** One immutable LUT is shared by the GPU preview and CPU JPEG exporter. */
class ColorLut private constructor(val size: Int, val colors: IntArray) {
    fun rgbaBytes(): ByteArray = ByteArray(colors.size * 4).also { bytes ->
        colors.forEachIndexed { i, color ->
            bytes[i * 4] = (color shr 16).toByte()
            bytes[i * 4 + 1] = (color shr 8).toByte()
            bytes[i * 4 + 2] = color.toByte()
            bytes[i * 4 + 3] = 255.toByte()
        }
    }

    fun apply(pixels: IntArray, count: Int = pixels.size) {
        require(count in 0..pixels.size)
        for (i in 0 until count) pixels[i] = sample(pixels[i])
    }

    fun sample(color: Int): Int {
        val scale = (size - 1) / 255f
        val r = ((color ushr 16) and 255) * scale
        val g = ((color ushr 8) and 255) * scale
        val b = (color and 255) * scale
        val ri = floor(r).toInt().coerceAtMost(size - 2)
        val gi = floor(g).toInt().coerceAtMost(size - 2)
        val bi = floor(b).toInt().coerceAtMost(size - 2)
        val rf = r - ri; val gf = g - gi; val bf = b - bi
        val base = (bi * size + gi) * size + ri
        val plane = size * size
        val c000 = colors[base]; val c100 = colors[base + 1]
        val c010 = colors[base + size]; val c110 = colors[base + size + 1]
        val c001 = colors[base + plane]; val c101 = colors[base + plane + 1]
        val c011 = colors[base + plane + size]; val c111 = colors[base + plane + size + 1]
        fun channel(shift: Int): Int {
            fun v(c: Int) = ((c ushr shift) and 255).toFloat()
            val low = mix(mix(v(c000), v(c100), rf), mix(v(c010), v(c110), rf), gf)
            val high = mix(mix(v(c001), v(c101), rf), mix(v(c011), v(c111), rf), gf)
            return mix(low, high, bf).roundToInt().coerceIn(0, 255)
        }
        return (color and -0x1000000) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
    }

    companion object {
        const val SIZE = 33
        val identity by lazy { create(GradeSettings(intensity = 0f)) }

        fun create(settings: GradeSettings): ColorLut {
            val look = Looks.find(settings.lookId)
            val strength = settings.intensity.coerceIn(0f, 1f)
            val temperature = look.warmth + settings.warmth.coerceIn(-1f, 1f) * .075f
            val contrast = look.contrast + settings.tone.coerceIn(-1f, 1f) * .22f
            val colors = IntArray(SIZE * SIZE * SIZE)
            for (b in 0 until SIZE) for (g in 0 until SIZE) for (r in 0 until SIZE) {
                val red = r / (SIZE - 1f); val green = g / (SIZE - 1f); val blue = b / (SIZE - 1f)
                val luma = .2126f * red + .7152f * green + .0722f * blue
                // A color-only skin heuristic softens saturation changes, not a face detector.
                val skin = (1f - abs((red - green) - .12f) * 5f).coerceIn(0f, 1f) *
                    ((green - blue) * 8f).coerceIn(0f, 1f) * ((luma - .12f) * 3f).coerceIn(0f, 1f)
                val saturation = mix(look.saturation, 1f, skin * .65f)
                fun grade(c: Float, wb: Float): Int {
                    var value = luma + (c - luma) * saturation
                    value = (value + wb * (1f - abs(value - .5f))).coerceIn(0f, 1f)
                    value = curve(value.pow(look.gamma), contrast)
                    value += look.fade * (1f - value) * (1f - value)
                    value -= look.highlight * value.pow(4)
                    return (mix(c, value.coerceIn(0f, 1f), strength) * 255).roundToInt().coerceIn(0, 255)
                }
                val outR = grade(red, temperature + look.tint * .3f)
                val outG = grade(green, -look.tint)
                val outB = grade(blue, -temperature)
                colors[(b * SIZE + g) * SIZE + r] = (255 shl 24) or (outR shl 16) or (outG shl 8) or outB
            }
            return ColorLut(SIZE, colors)
        }

        /** Contrast that keeps black and white in place: slope [contrast] at mid grey, a soft toe and shoulder. */
        private fun curve(value: Float, contrast: Float) =
            if (value < .5f) .5f * (2f * value).pow(contrast) else 1f - .5f * (2f * (1f - value)).pow(contrast)

        private fun mix(a: Float, b: Float, f: Float) = a + (b - a) * f
    }
}
