package studio.moment.camera.data

import android.content.Context
import studio.moment.camera.camera.FrameRatio
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.color.Looks

data class Preferences(
    val grade: GradeSettings = GradeSettings(),
    val grid: Boolean = true,
    val saveOriginal: Boolean = true,
    val mirrorSelfie: Boolean = true,
    val haptics: Boolean = true,
    val volumeShutter: Boolean = true,
    val ratio: FrameRatio = FrameRatio.CLASSIC,
    val timer: Int = 0,
)

class PreferenceStore(context: Context) {
    private val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
    fun read() = Preferences(
        grade = GradeSettings(Looks.find(prefs.getString("look", "xs") ?: "xs").id, safeFloat("intensity", 1f, 0f..1f),
            safeFloat("warmth", 0f, -1f..1f), safeFloat("tone", 0f, -1f..1f)),
        grid = prefs.getBoolean("grid", true), saveOriginal = prefs.getBoolean("original", true),
        mirrorSelfie = prefs.getBoolean("mirror", true), haptics = prefs.getBoolean("haptics", true),
        volumeShutter = prefs.getBoolean("volumeShutter", true),
        ratio = FrameRatio.entries.firstOrNull { it.name == prefs.getString("ratio", "CLASSIC") } ?: FrameRatio.CLASSIC,
        timer = prefs.getInt("timer", 0).takeIf { it in listOf(0, 3, 10) } ?: 0,
    )
    fun write(settings: Preferences) {
        prefs.edit().putString("look", settings.grade.lookId).putFloat("intensity", settings.grade.intensity)
            .putFloat("warmth", settings.grade.warmth).putFloat("tone", settings.grade.tone)
            .putBoolean("grid", settings.grid).putBoolean("original", settings.saveOriginal)
            .putBoolean("mirror", settings.mirrorSelfie).putBoolean("haptics", settings.haptics)
            .putBoolean("volumeShutter", settings.volumeShutter)
            .putString("ratio", settings.ratio.name).putInt("timer", settings.timer).apply()
    }
    var latestUri: String?
        get() = prefs.getString("latest", null)
        set(value) { prefs.edit().putString("latest", value).apply() }

    private fun safeFloat(key: String, fallback: Float, range: ClosedFloatingPointRange<Float>) =
        prefs.getFloat(key, fallback).takeIf { it.isFinite() }?.coerceIn(range) ?: fallback
}
