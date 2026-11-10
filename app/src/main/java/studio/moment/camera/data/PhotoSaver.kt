package studio.moment.camera.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.GradeSettings
import java.io.File

/** One save queue for the whole process, so saving goes on when the camera screen is recreated or closed. */
object PhotoSaver {
    @Volatile private var queue: PhotoQueue<SavedPhoto>? = null

    fun queue(context: Context): PhotoQueue<SavedPhoto> = queue ?: synchronized(this) {
        queue ?: create(context.applicationContext).also { queue = it }
    }

    private fun create(app: Context): PhotoQueue<SavedPhoto> {
        val photos = PhotoStore(app)
        val storage = PreferenceStore(app)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        // Only the queue's single worker touches this; a burst with one style builds its table once.
        var lastLut: Pair<GradeSettings, ColorLut>? = null
        // The latest photo is recorded here as well: the camera screen may be gone when a photo finishes.
        fun remember(saved: SavedPhoto) = saved.also { storage.latestUri = it.uri.toString() }
        val queue = PhotoQueue(File(app.filesDir, "pending"), scope,
            savePlain = { photo -> remember(photos.publishUnprocessed(photo.file)) }) { photo ->
            val lut = lastLut?.takeIf { it.first == photo.grade }?.second
                ?: ColorLut.create(photo.grade).also { lastLut = photo.grade to it }
            remember(photos.export(photo.file, lut, photo.grade, photo.saveOriginal, photo.frame, photo.quality))
        }
        scope.launch(Dispatchers.IO) { queue.resume() }
        return queue
    }
}
