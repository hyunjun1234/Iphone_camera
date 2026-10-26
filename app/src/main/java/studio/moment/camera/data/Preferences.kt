package studio.moment.camera.data

import android.content.Context
import studio.moment.camera.camera.FrameRatio
import studio.moment.camera.color.GradeSettings

data class Preferences(
    val grade: GradeSettings = GradeSettings(),
    val grid: Boolean = true,
    val saveOriginal: Boolean = true,
    val mirrorSelfie: Boolean = true,
    val haptics: Boolean = true,
    val ratio: FrameRatio = FrameRatio.CLASSIC,
    val timer: Int = 0,
)

class PreferenceStore(context: Context) {
    private val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
    fun read() = Preferences(
        grade = GradeSettings(prefs.getString("look", "xs") ?: "xs", prefs.getFloat("intensity", 1f).coerceIn(0f, 1f),
            prefs.getFloat("warmth", 0f).coerceIn(-1f, 1f), prefs.getFloat("tone", 0f).coerceIn(-1f, 1f)),
        grid = prefs.getBoolean("grid", true), saveOriginal = prefs.getBoolean("original", true),
        mirrorSelfie = prefs.getBoolean("mirror", true), haptics = prefs.getBoolean("haptics", true),
        ratio = FrameRatio.entries.firstOrNull { it.name == prefs.getString("ratio", "CLASSIC") } ?: FrameRatio.CLASSIC,
        timer = prefs.getInt("timer", 0).takeIf { it in listOf(0, 3, 10) } ?: 0,
    )
    fun write(settings: Preferences) {
        prefs.edit().putString("look", settings.grade.lookId).putFloat("intensity", settings.grade.intensity)
            .putFloat("warmth", settings.grade.warmth).putFloat("tone", settings.grade.tone)
            .putBoolean("grid", settings.grid).putBoolean("original", settings.saveOriginal)
            .putBoolean("mirror", settings.mirrorSelfie).putBoolean("haptics", settings.haptics)
            .putString("ratio", settings.ratio.name).putInt("timer", settings.timer).apply()
    }
    var latestUri: String?
        get() = prefs.getString("latest", null)
        set(value) { prefs.edit().putString("latest", value).apply() }
}
