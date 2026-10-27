package studio.moment.camera.camera

/** Shortcuts use the active camera's limits; 10x is a shortcut, never a zoom cap. */
fun zoomSteps(minimum: Float, maximum: Float): List<Float> {
    require(minimum.isFinite() && maximum.isFinite() && minimum > 0 && maximum >= minimum)
    return (listOf(minimum) + listOf(1f, 2f, 3f, 5f, 10f, 30f, 100f) + maximum)
        .filter { it in minimum..maximum }.distinct().sorted()
}
