package studio.moment.camera.color

/** JPEG has no alpha. Use the same white background for the editor preview and export. */
fun compositeOnWhite(pixels: IntArray, count: Int = pixels.size) {
    for (index in 0 until count) {
        val pixel = pixels[index]
        val alpha = pixel ushr 24
        if (alpha == 255) continue
        fun channel(shift: Int) = ((((pixel ushr shift) and 255) * alpha + 255 * (255 - alpha) + 127) / 255)
        pixels[index] = (255 shl 24) or (channel(16) shl 16) or (channel(8) shl 8) or channel(0)
    }
}
