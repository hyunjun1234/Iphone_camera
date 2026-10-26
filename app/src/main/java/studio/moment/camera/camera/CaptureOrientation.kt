package studio.moment.camera.camera

/** Rotation constants match android.view.Surface. Keep the last direction near a boundary. */
object CaptureOrientation {
    fun fromSensor(degrees: Int, previous: Int = 0): Int {
        if (degrees !in 0..359) return previous
        val boundaryDistance = kotlin.math.abs(degrees % 90 - 45)
        if (boundaryDistance < 10) return previous
        return when (degrees) {
            in 45..134 -> 3
            in 135..224 -> 2
            in 225..314 -> 1
            else -> 0
        }
    }
}
