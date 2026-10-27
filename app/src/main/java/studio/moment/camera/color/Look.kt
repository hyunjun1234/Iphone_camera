package studio.moment.camera.color

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

/** Creative interpretations, not measured Apple camera profiles. All values operate in SDR sRGB. */
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
        Look("6s", "6s", "2015", "부드러운 크림색", "노란 기를 더하고 채도와 대비를 낮춘 스타일이에요.", 0xFFE2C6A4, .045f, .008f, .86f, .94f, .035f, .08f, .98f),
        Look("7", "7", "2016", "은은한 금빛", "따뜻한 색에 적당한 대비를 더한 스타일이에요.", 0xFFD9BD7F, .032f, -.004f, .94f, 1.04f, .018f, .06f),
        Look("8", "8", "2017", "따뜻하고 또렷한 색", "따뜻한 색에 은은한 대비를 더하고 밝은 부분을 부드럽게 표현해요.", 0xFFD4BEA8, .026f, .006f, .98f, 1.035f, .012f, .085f, .99f),
        Look("x", "X", "2017", "또렷한 명암", "그림자를 깊게 표현하고 색을 선명하게 살려요.", 0xFFC0B4A0, .014f, .006f, .97f, 1.10f, .008f, .065f),
        Look("xs", "XS", "2018", "따뜻하고 부드러운 색", "노란 기를 살짝 더하고 밝은 부분을 부드럽게 표현해요.", 0xFFD9CFAD, .038f, .004f, .91f, .98f, .022f, .11f, .97f),
        Look("11", "11", "2019", "자연스러운 색", "채도를 크게 바꾸지 않고 어두운 부분을 살짝 밝게 해요.", 0xFFB7C5AD, .014f, -.004f, .98f, 1.02f, .014f, .08f, .97f),
        Look("13", "13", "2021", "선명한 색", "채도와 대비를 높이고 색온도를 조금 낮춘 스타일이에요.", 0xFFAFC3CC, -.006f, .002f, 1.06f, 1.09f, .004f, .09f),
        Look("15pro", "15 Pro", "2023", "차분한 색", "채도를 살짝 낮추고 밝은 부분의 대비를 줄여요.", 0xFFC4BCAF, .003f, .001f, .97f, 1.055f, .008f, .105f),
        Look("16", "16", "2024", "밝고 부드러운 색", "중간 밝기를 올리고 밝은 부분을 부드럽게 표현해요.", 0xFFC6C7B5, .008f, .003f, 1.025f, 1.035f, .009f, .12f, .97f),
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
                    value = value.pow(look.gamma)
                    value = ((value - .5f) * contrast + .5f).coerceIn(0f, 1f)
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

        private fun mix(a: Float, b: Float, f: Float) = a + (b - a) * f
    }
}
