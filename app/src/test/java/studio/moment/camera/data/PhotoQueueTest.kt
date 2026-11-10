package studio.moment.camera.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import studio.moment.camera.camera.CaptureFrame
import studio.moment.camera.color.GradeSettings
import java.io.File
import java.io.IOException
import java.nio.file.Files

class PhotoQueueTest {
    private val root = Files.createTempDirectory("photo-queue").toFile()
    private val scopes = mutableListOf<CoroutineScope>()

    @After fun cleanUp() { scopes.forEach { it.cancel() }; root.deleteRecursively() }

    private fun scope() = CoroutineScope(SupervisorJob() + Dispatchers.Default).also { scopes += it }
    private fun capture(name: String) = File(root, "$name.jpg").apply { writeText(name) }
    private fun pendingFolder() = File(root, "pending")

    @Test fun shutterDoesNotWaitForColourProcessing() = runBlocking<Unit> {
        val release = CompletableDeferred<Unit>()
        val queue = PhotoQueue(pendingFolder(), scope()) { release.await(); "saved" }
        // add() returning at all proves the caller is free while the export is still blocked.
        withTimeout(5_000) { queue.add(capture("first"), GradeSettings(), true, null, PhotoQuality.STANDARD) }
        assertEquals(1, queue.pending.value)
        release.complete(Unit)
        withTimeout(5_000) { queue.pending.first { it == 0 } }
    }

    @Test fun photosAreSavedInTheOrderTheyWereTakenAndTheLastOneReportsTheBurst() = runBlocking<Unit> {
        val release = CompletableDeferred<Unit>()
        val saved = mutableListOf<String>()
        val queue = PhotoQueue(pendingFolder(), scope()) { photo ->
            release.await()
            photo.file.readText().also { synchronized(saved) { saved += it } }
        }
        val events = mutableListOf<QueueEvent<String>>()
        val collecting = CompletableDeferred<Unit>()
        val collector = launch(Dispatchers.Default) {
            queue.events.onSubscription { collecting.complete(Unit) }.collect { synchronized(events) { events += it } }
        }
        collecting.await()
        for (name in listOf("one", "two", "three")) queue.add(capture(name), GradeSettings(), false, null, PhotoQuality.STANDARD)
        assertEquals(3, queue.pending.value)
        release.complete(Unit)
        withTimeout(5_000) { queue.pending.first { it == 0 } }
        withTimeout(5_000) { while (synchronized(events) { events.size } < 3) kotlinx.coroutines.yield() }
        collector.cancel()
        assertEquals(listOf("one", "two", "three"), saved)
        val results = events.filterIsInstance<QueueEvent.Saved<String>>()
        assertEquals(listOf("one", "two", "three"), results.map { it.result })
        // Only the photo that empties the queue announces how many were saved together.
        assertEquals(listOf(2, 1, 0), results.map { it.remaining })
        assertEquals(3, results.last().savedTogether)
    }

    @Test fun aPhotoWaitingWhenTheAppDiesIsSavedWithItsSettingsOnTheNextStart() = runBlocking<Unit> {
        val never = CompletableDeferred<Unit>()
        val dying = scope()
        val first = PhotoQueue(pendingFolder(), dying) { never.await(); "unreachable" }
        val grade = GradeSettings("6s", .42f, -.5f, .25f)
        val frame = CaptureFrame(.125f, 0f, .875f, 1f, .5625f)
        first.add(capture("survivor"), grade, true, frame, PhotoQuality.HIGH)
        dying.cancel()

        // The file is removed once it has been saved, so its content is read while it is being saved.
        val seen = CompletableDeferred<Pair<PendingPhoto, String>>()
        val second = PhotoQueue(pendingFolder(), scope()) { photo -> photo.file.readText().also { seen.complete(photo to it) } }
        second.resume()
        val (photo, content) = withTimeout(5_000) { seen.await() }
        assertEquals("survivor", content)
        assertEquals(grade, photo.grade)
        assertEquals(frame, photo.frame)
        assertTrue(photo.saveOriginal)
        assertEquals(PhotoQuality.HIGH, photo.quality)
        withTimeout(5_000) { second.pending.first { it == 0 } }
        assertEquals("Nothing may be left behind after a successful save", emptyList<String>(), pendingFolder().list()!!.toList())
    }

    @Test fun aFailedSaveKeepsThePhotoAndTheNextStartSavesIt() = runBlocking<Unit> {
        val failing = PhotoQueue<String>(pendingFolder(), scope()) { throw IOException("disk full") }
        val failure = CompletableDeferred<QueueEvent<String>>()
        val collecting = CompletableDeferred<Unit>()
        val collector = launch(Dispatchers.Default) {
            failure.complete(failing.events.onSubscription { collecting.complete(Unit) }.first())
        }
        collecting.await()
        failing.add(capture("kept"), GradeSettings(), false, null, PhotoQuality.SMALL)
        val event = withTimeout(5_000) { failure.await() }
        collector.cancel()
        assertTrue(event is QueueEvent.Failed)
        assertEquals("disk full", (event as QueueEvent.Failed).error.message)
        withTimeout(5_000) { failing.pending.first { it == 0 } }

        val retried = CompletableDeferred<Pair<PendingPhoto, String>>()
        val next = PhotoQueue(pendingFolder(), scope()) { photo -> photo.file.readText().also { retried.complete(photo to it) } }
        next.resume()
        val (photo, content) = withTimeout(5_000) { retried.await() }
        assertEquals("kept", content)
        // The count lets the app fall back to saving the untouched capture instead of failing forever.
        assertEquals(1, photo.attempts)
    }

    @Test fun afterTwoFailedStartsThePhotoIsSavedAsItCameFromTheCamera() = runBlocking<Unit> {
        val broken: suspend (PendingPhoto) -> String = { throw IOException("cannot process") }
        // Two runs fail to process the photo.
        for (run in 1..2) {
            val queue = PhotoQueue(pendingFolder(), scope(), savePlain = { "plain copy must wait for the third start" }, export = broken)
            val failed = CompletableDeferred<QueueEvent<String>>()
            val collecting = CompletableDeferred<Unit>()
            val collector = launch(Dispatchers.Default) {
                failed.complete(queue.events.onSubscription { collecting.complete(Unit) }.first())
            }
            collecting.await()
            if (run == 1) queue.add(capture("stubborn"), GradeSettings(), true, null, PhotoQuality.STANDARD) else queue.resume()
            assertTrue("run $run must fail", withTimeout(5_000) { failed.await() } is QueueEvent.Failed)
            collector.cancel()
        }
        // The third run stops trying to process it and keeps the picture.
        val plain = CompletableDeferred<String>()
        val last = PhotoQueue(pendingFolder(), scope(), savePlain = { photo -> photo.file.readText().also(plain::complete) }, export = broken)
        last.resume()
        assertEquals("stubborn", withTimeout(5_000) { plain.await() })
        withTimeout(5_000) { last.pending.first { it == 0 } }
        assertEquals(emptyList<String>(), pendingFolder().list()!!.toList())
    }

    @Test fun aPhotoWithoutACropKeepsNoFrame() = runBlocking<Unit> {
        val seen = CompletableDeferred<PendingPhoto>()
        val dying = scope()
        val never = CompletableDeferred<Unit>()
        PhotoQueue(pendingFolder(), dying) { never.await(); "unreachable" }
            .add(capture("whole"), GradeSettings(), false, null, PhotoQuality.STANDARD)
        dying.cancel()
        PhotoQueue(pendingFolder(), scope()) { photo -> seen.complete(photo); "saved" }.resume()
        val photo = withTimeout(5_000) { seen.await() }
        assertNull(photo.frame)
        assertFalse(photo.saveOriginal)
    }
}
