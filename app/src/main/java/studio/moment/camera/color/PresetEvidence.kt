package studio.moment.camera.color

/** References explain capabilities and qualitative observations; they are not LUT calibration data. */
data class PresetEvidence(val specification: String, val review: String, val reviewModel: String)

fun Look.evidence(): PresetEvidence {
    val apple = "https://support.apple.com/en-us/"
    val dxo = "https://www.dxomark.com/"
    val (spec, review) = when (id) {
        "6s" -> "111952" to "apple-iphone-6s-review-solid-and-compelling-performance/"
        "7" -> "111943" to "apple-iphone-7-camera-review-better-than-ever/"
        "x" -> "111864" to "apple-iphone-x-top-performer-stills/"
        "xs" -> "111881" to "apple-iphone-xs-max-review-flagship-imaging-power/"
        "11" -> "111865" to "apple-iphone-11-camera-review/"
        "13" -> "111872" to "apple-iphone-13-camera-review-reliable-imaging-companion/"
        "15pro" -> "111829" to "apple-iphone-15-pro-camera-test/"
        else -> "121029" to "apple-iphone-16-camera-test/"
    }
    return PresetEvidence(apple + spec, dxo + review, if (id == "xs") "아이폰 XS Max" else "아이폰 $model")
}
