package studio.moment.camera.data

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.moment.camera.camera.CaptureFrame
import studio.moment.camera.color.GradeSettings
import java.io.File
import java.util.Collections
import java.util.Properties
import java.util.UUID

/** A captured photo waiting to be saved, with everything needed to finish it after a restart. */
data class PendingPhoto(
    val file: File, val grade: GradeSettings, val saveOriginal: Boolean, val frame: CaptureFrame?,
    val quality: PhotoQuality, val attempts: Int = 0,
)

sealed interface QueueEvent<out R> {
    /** [savedTogether] counts the photos saved since the queue was last empty. */
    data class Saved<R>(val photo: PendingPhoto, val result: R, val remaining: Int, val savedTogether: Int) : QueueEvent<R>
    data class Failed(val photo: PendingPhoto, val error: Throwable, val remaining: Int) : QueueEvent<Nothing>
}

/**
 * Saves photos one at a time, apart from the camera, so the shutter never waits for colour processing.
 * A photo is first moved into [directory] with its settings; it stays there until it has been saved,
 * which lets the next start finish what an earlier run could not. After two failed starts [savePlain]
 * stores the camera's own file instead.
 */
class PhotoQueue<R>(
    private val directory: File, scope: CoroutineScope,
    private val savePlain: (suspend (PendingPhoto) -> R)? = null,
    private val export: suspend (PendingPhoto) -> R,
) {
    private val mutablePending = MutableStateFlow(0)
    val pending = mutablePending.asStateFlow()
    private val mutableEvents = MutableSharedFlow<QueueEvent<R>>(extraBufferCapacity = 64)
    val events = mutableEvents.asSharedFlow()
    private val waiting = Channel<PendingPhoto>(Channel.UNLIMITED)
    private val known = Collections.synchronizedSet(mutableSetOf<String>())

    init {
        scope.launch {
            var savedTogether = 0
            for (photo in waiting) {
                val event = try {
                    // Two failed starts mean processing will not succeed: keep the picture rather than the look.
                    val result = savePlain?.takeIf { photo.attempts >= 2 }?.invoke(photo) ?: export(photo)
                    photo.file.delete(); settingsOf(photo.file).delete()
                    known -= photo.file.name
                    QueueEvent.Saved(photo, result, mutablePending.updateAndGet { it - 1 }, ++savedTogether)
                } catch (cancel: CancellationException) { throw cancel }
                catch (error: Throwable) {
                    // The photo stays on disk for the next start; trying again now would fail the same way.
                    runCatching { write(photo.copy(attempts = photo.attempts + 1)) }
                    QueueEvent.Failed(photo, error, mutablePending.updateAndGet { it - 1 })
                }
                if (mutablePending.value == 0) savedTogether = 0
                mutableEvents.emit(event)
            }
        }
    }

    suspend fun add(capture: File, grade: GradeSettings, saveOriginal: Boolean, frame: CaptureFrame?, quality: PhotoQuality) =
        withContext(Dispatchers.IO) {
            directory.mkdirs()
            val name = "%013d-%s.jpg".format(System.currentTimeMillis(), UUID.randomUUID().toString().take(8))
            val photo = PendingPhoto(File(directory, name), grade, saveOriginal, frame, quality)
            // Settings go first: settings without a photo are harmless, a photo without settings is not.
            write(photo)
            if (!capture.renameTo(photo.file)) { capture.copyTo(photo.file, overwrite = true); capture.delete() }
            enqueue(photo)
        }

    /** Queues the photos an earlier run left behind, oldest first. */
    fun resume() {
        val files = directory.listFiles() ?: return
        val photos = files.filter { it.extension == "jpg" }.sortedBy { it.name }
        files.filter { it.extension == "txt" && photos.none { photo -> settingsOf(photo) == it } }.forEach { it.delete() }
        photos.forEach { enqueue(read(it)) }
    }

    private fun enqueue(photo: PendingPhoto) {
        if (!known.add(photo.file.name)) return
        mutablePending.update { it + 1 }
        waiting.trySend(photo)
    }

    private fun settingsOf(photo: File) = File(photo.parentFile, photo.nameWithoutExtension + ".txt")

    private fun write(photo: PendingPhoto) {
        val values = Properties().apply {
            setProperty("look", photo.grade.lookId)
            setProperty("intensity", photo.grade.intensity.toString())
            setProperty("warmth", photo.grade.warmth.toString())
            setProperty("tone", photo.grade.tone.toString())
            setProperty("original", photo.saveOriginal.toString())
            setProperty("quality", photo.quality.name)
            setProperty("attempts", photo.attempts.toString())
            photo.frame?.let { frame ->
                setProperty("frame", listOf(frame.left, frame.top, frame.right, frame.bottom).joinToString(","))
                frame.aspectRatio?.let { setProperty("frameRatio", it.toString()) }
            }
        }
        settingsOf(photo.file).outputStream().use { values.store(it, null) }
    }

    /** Unreadable settings fall back to defaults: saving the photo plainly beats losing it. */
    private fun read(file: File): PendingPhoto {
        val values = Properties()
        runCatching { settingsOf(file).inputStream().use(values::load) }
        fun float(key: String, fallback: Float) = values.getProperty(key)?.toFloatOrNull()?.takeIf { it.isFinite() } ?: fallback
        val defaults = GradeSettings()
        val frame = runCatching {
            values.getProperty("frame")?.split(",")?.map { it.toFloat() }?.let { edges ->
                CaptureFrame(edges[0], edges[1], edges[2], edges[3], values.getProperty("frameRatio")?.toFloat())
            }
        }.getOrNull()
        return PendingPhoto(
            file,
            GradeSettings(values.getProperty("look") ?: defaults.lookId, float("intensity", defaults.intensity),
                float("warmth", defaults.warmth), float("tone", defaults.tone)),
            values.getProperty("original").toBoolean(), frame,
            PhotoQuality.entries.firstOrNull { it.name == values.getProperty("quality") } ?: PhotoQuality.STANDARD,
            values.getProperty("attempts")?.toIntOrNull() ?: 0,
        )
    }
}
