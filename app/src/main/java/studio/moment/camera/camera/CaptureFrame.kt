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

    /** Where the whole camera image sits on screen when this frame is shown at the given bounds. */
    fun sceneAround(frameLeft: Float, frameTop: Float, frameWidth: Float, frameHeight: Float): SceneBounds {
        val width = frameWidth / (right - left)
        val height = frameHeight / (bottom - top)
        return SceneBounds(frameLeft - left * width, frameTop - top * height, width, height)
    }

    /** A touch inside the displayed frame, as a position in the view that shows the whole camera image. */
    fun scenePoint(x: Float, y: Float, frameWidth: Float, frameHeight: Float): Pair<Float, Float> =
        sceneAround(0f, 0f, frameWidth, frameHeight).let { x - it.left to y - it.top }

    companion object {
        /** The largest centred frame of [frameRatio] in a camera image of [sceneRatio]; both are width / height. */
        fun centered(frameRatio: Float, sceneRatio: Float): CaptureFrame {
            val width = minOf(1f, frameRatio / sceneRatio)
            val height = minOf(1f, sceneRatio / frameRatio)
            return CaptureFrame((1f - width) / 2f, (1f - height) / 2f, (1f + width) / 2f, (1f + height) / 2f, frameRatio)
        }
    }
}

data class SceneBounds(val left: Float, val top: Float, val width: Float, val height: Float)

/**
 * Width to height of the camera image as seen on screen. CameraX streams default to 4:3, the whole
 * sensor on nearly every phone; [sensorRotationDegrees] tells whether the screen shows it turned upright.
 */
fun cameraImageShape(sensorRotationDegrees: Int): Pair<Int, Int> = if (sensorRotationDegrees % 180 == 0) 4 to 3 else 3 to 4

data class PixelFrame(val left: Int, val top: Int, val width: Int, val height: Int)
data class CapturedPhoto(val file: File, val frame: CaptureFrame)
