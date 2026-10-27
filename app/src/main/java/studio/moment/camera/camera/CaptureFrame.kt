package studio.moment.camera.camera

import java.io.File
import kotlin.math.roundToInt

/** The photographed area, in normalized coordinates of the shared camera viewport. */
data class CaptureFrame(val left: Float, val top: Float, val right: Float, val bottom: Float, val aspectRatio: Float? = null) {
    init {
        require(listOf(left, top, right, bottom).all { it.isFinite() })
        require(left >= 0f && top >= 0f && right <= 1f && bottom <= 1f && left < right && top < bottom)
        require(aspectRatio == null || (aspectRatio.isFinite() && aspectRatio > 0f))
    }

    fun mirrorHorizontally() = CaptureFrame(1f - right, top, 1f - left, bottom, aspectRatio)

    fun rotateClockwise(quarterTurns: Int): CaptureFrame = when ((quarterTurns % 4 + 4) % 4) {
        1 -> CaptureFrame(1f - bottom, left, 1f - top, right, aspectRatio?.let { 1f / it })
        2 -> CaptureFrame(1f - right, 1f - bottom, 1f - left, 1f - top, aspectRatio)
        3 -> CaptureFrame(top, 1f - right, bottom, 1f - left, aspectRatio?.let { 1f / it })
        else -> this
    }

    fun pixels(width: Int, height: Int): PixelFrame {
        require(width > 0 && height > 0)
        val x = (left * width).roundToInt().coerceIn(0, width - 1)
        val y = (top * height).roundToInt().coerceIn(0, height - 1)
        val endX = (right * width).roundToInt().coerceIn(x + 1, width)
        val endY = (bottom * height).roundToInt().coerceIn(y + 1, height)
        var cropWidth = endX - x
        var cropHeight = endY - y
        aspectRatio?.let { ratio ->
            // Preview/sensor sizes round independently; trim at most their rounding difference.
            if (cropWidth.toFloat() / cropHeight > ratio) cropWidth = (cropHeight * ratio).roundToInt().coerceIn(1, cropWidth)
            else cropHeight = (cropWidth / ratio).roundToInt().coerceIn(1, cropHeight)
        }
        return PixelFrame(x + (endX - x - cropWidth) / 2, y + (endY - y - cropHeight) / 2, cropWidth, cropHeight)
    }
}

data class PixelFrame(val left: Int, val top: Int, val width: Int, val height: Int)
data class CapturedPhoto(val file: File, val frame: CaptureFrame)
