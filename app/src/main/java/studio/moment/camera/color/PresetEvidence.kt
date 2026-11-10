package studio.moment.camera.color

/**
 * [chart] is the review whose colour chart photo was measured for the style values; null when that photo does not
 * show the main camera and neighbouring models were used instead. [specification] and [review] describe the camera.
 */
data class PresetEvidence(val specification: String, val review: String, val reviewModel: String, val chart: String?)

fun Look.evidence(): PresetEvidence {
    val apple = "https://support.apple.com/en-us/"
    val dxo = "https://www.dxomark.com/"
    val notebookcheck = "https://www.notebookcheck.net/"
    val (spec, review) = when (id) {
        "6s" -> "111952" to "apple-iphone-6s-review-solid-and-compelling-performance/"
        "7" -> "111943" to "apple-iphone-7-camera-review-better-than-ever/"
        "8" -> "111976" to "apple-iphone-8-reviewed-a-solid-performance-upgrade-over-the-iphone-7/"
        "x" -> "111864" to "apple-iphone-x-top-performer-stills/"
        "xs" -> "111881" to "apple-iphone-xs-max-review-flagship-imaging-power/"
        "11" -> "111865" to "apple-iphone-11-camera-review/"
        "13" -> "111872" to "apple-iphone-13-camera-review-reliable-imaging-companion/"
        "15pro" -> "111829" to "apple-iphone-15-pro-camera-test/"
        else -> "121029" to "apple-iphone-16-camera-test/"
    }
    val chart = when (id) {
        "6s" -> "Apple-iPhone-6S-Smartphone-Review.151403.0.html"
        "7" -> "Apple-iPhone-7-Smartphone-Review.173851.0.html"
        "8" -> "Apple-iPhone-8-Smartphone-Review.250930.0.html"
        "x" -> "Apple-iPhone-X-Smartphone-Review.264436.0.html"
        "xs" -> "Apple-iPhone-XS-Smartphone-Review.333546.0.html"
        "11" -> "Apple-iPhone-11-Smartphone-Review-More-than-just-an-affordable-Apple-smartphone.435087.0.html"
        "13" -> "Apple-iPhone-13-Smartphone-Review-High-quality-Apple-phone-with-an-even-brighter-display.572153.0.html"
        "16" -> "Apple-iPhone-16-smartphone-review-More-innovations-than-one-would-think.908118.0.html"
        else -> null
    }
    return PresetEvidence(apple + spec, dxo + review, if (id == "xs") "아이폰 XS Max" else "아이폰 $model", chart?.let { notebookcheck + it })
}
