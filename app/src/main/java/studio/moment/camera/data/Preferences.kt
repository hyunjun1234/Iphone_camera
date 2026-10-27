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
    val photoQuality: PhotoQuality = PhotoQuality.STANDARD,
)

class PreferenceStore(context: Context) {
    private val prefs = context.getSharedPreferences("moment", Context.MODE_PRIVATE)
    fun read() = Preferences(
        grade = readGrade(prefs.getString("look", "xs") ?: "xs"),
        grid = prefs.getBoolean("grid", true), saveOriginal = prefs.getBoolean("original", true),
        mirrorSelfie = prefs.getBoolean("mirror", true), haptics = prefs.getBoolean("haptics", true),
        volumeShutter = prefs.getBoolean("volumeShutter", true),
        ratio = FrameRatio.entries.firstOrNull { it.name == prefs.getString("ratio", "CLASSIC") } ?: FrameRatio.CLASSIC,
        timer = prefs.getInt("timer", 0).takeIf { it in listOf(0, 3, 10) } ?: 0,
        photoQuality = PhotoQuality.entries.firstOrNull { it.name == prefs.getString("photoQuality", null) } ?: PhotoQuality.STANDARD,
    )
    fun readGrade(lookId: String): GradeSettings {
        val id = Looks.find(lookId).id
        val prefix = "grade.$id."
        // Preserve the active style's adjustments when upgrading from the single-style format.
        val keyPrefix = when {
            prefs.contains("${prefix}intensity") -> prefix
            id == Looks.find(prefs.getString("look", "xs") ?: "xs").id -> ""
            else -> return GradeSettings(lookId = id)
        }
        return GradeSettings(id, safeFloat("${keyPrefix}intensity", 1f, 0f..1f),
            safeFloat("${keyPrefix}warmth", 0f, -1f..1f), safeFloat("${keyPrefix}tone", 0f, -1f..1f))
    }

    fun write(settings: Preferences) {
        prefs.edit().putString("look", settings.grade.lookId).putFloat("intensity", settings.grade.intensity)
            .putFloat("warmth", settings.grade.warmth).putFloat("tone", settings.grade.tone)
            .putFloat("grade.${settings.grade.lookId}.intensity", settings.grade.intensity)
            .putFloat("grade.${settings.grade.lookId}.warmth", settings.grade.warmth)
            .putFloat("grade.${settings.grade.lookId}.tone", settings.grade.tone)
            .putBoolean("grid", settings.grid).putBoolean("original", settings.saveOriginal)
            .putBoolean("mirror", settings.mirrorSelfie).putBoolean("haptics", settings.haptics)
            .putBoolean("volumeShutter", settings.volumeShutter)
            .putString("ratio", settings.ratio.name).putInt("timer", settings.timer)
            .putString("photoQuality", settings.photoQuality.name).apply()
    }
    var latestUri: String?
        get() = prefs.getString("latest", null)
        set(value) { prefs.edit().putString("latest", value).apply() }

    private fun safeFloat(key: String, fallback: Float, range: ClosedFloatingPointRange<Float>) =
        prefs.getFloat(key, fallback).takeIf { it.isFinite() }?.coerceIn(range) ?: fallback
}
