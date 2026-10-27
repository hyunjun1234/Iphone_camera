package studio.moment.camera

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.moment.camera.camera.CameraEngine
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.compositeOnWhite
import studio.moment.camera.data.PhotoStore
import studio.moment.camera.data.PreferenceStore
import studio.moment.camera.data.Preferences
import studio.moment.camera.data.userMessage
import java.io.File

enum class WorkStage(val label: String) {
    IDLE(""), IMPORTING("사진을 불러오는 중이에요"), COUNTDOWN("촬영을 준비하고 있어요"),
    CAPTURING("사진을 촬영하는 중이에요"), SAVING("사진을 저장하는 중이에요")
}

class MomentViewModel(app: Application) : AndroidViewModel(app) {
    private val storage = PreferenceStore(app)
    private val photos = PhotoStore(app)
    private val mutablePreferences = MutableStateFlow(storage.read())
    val preferences = mutablePreferences.asStateFlow()
    private val mutableLut = MutableStateFlow(ColorLut.identity)
    val lut = mutableLut.asStateFlow()
    private val mutableGradeReady = MutableStateFlow(false)
    val gradeReady = mutableGradeReady.asStateFlow()
    private val mutableBusy = MutableStateFlow(false)
    val busy = mutableBusy.asStateFlow()
    private val mutableStage = MutableStateFlow(WorkStage.IDLE)
    val stage = mutableStage.asStateFlow()
    private val mutableCountdown = MutableStateFlow(0)
    val countdown = mutableCountdown.asStateFlow()
    private val mutableMessage = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = mutableMessage.asSharedFlow()
    private val mutableLatest = MutableStateFlow(storage.latestUri?.let(Uri::parse))
    val latest = mutableLatest.asStateFlow()
    private val mutableImported = MutableStateFlow<File?>(null)
    val imported = mutableImported.asStateFlow()
    private val mutableImportPreview = MutableStateFlow<Bitmap?>(null)
    val importPreview = mutableImportPreview.asStateFlow()
    private val mutableImportOriginal = MutableStateFlow<Bitmap?>(null)
    val importOriginal = mutableImportOriginal.asStateFlow()
    private var captureJob: Job? = null
    private var importJob: Job? = null

    init {
        viewModelScope.launch { runCatching { photos.cleanStaleWork() } }
        viewModelScope.launch {
            combine(mutablePreferences.map { it.grade }.distinctUntilChanged(), mutableImportOriginal) { grade, original -> grade to original }
                .collectLatest { (grade, original) ->
                    mutableGradeReady.value = false
                    try {
                        val (nextLut, preview) = withContext(Dispatchers.Default) {
                            val lut = ColorLut.create(grade)
                            val preview = original?.copy(Bitmap.Config.ARGB_8888, true)
                            try {
                                if (preview != null) {
                                    val pixels = IntArray(preview.width * 32)
                                    for (y in 0 until preview.height step 32) {
                                        ensureActive()
                                        val rows = minOf(32, preview.height - y)
                                        preview.getPixels(pixels, 0, preview.width, 0, y, preview.width, rows)
                                        compositeOnWhite(pixels, preview.width * rows)
                                        lut.apply(pixels, preview.width * rows)
                                        preview.setPixels(pixels, 0, preview.width, 0, y, preview.width, rows)
                                    }
                                }
                                lut to preview
                            } catch (e: Throwable) { preview?.recycle(); throw e }
                        }
                        // Publish matching preview and export LUT before allowing the save button.
                        mutableLut.value = nextLut
                        mutableImportPreview.value = preview
                        mutableGradeReady.value = true
                    } catch (cancel: CancellationException) { throw cancel }
                    catch (e: OutOfMemoryError) { notice(userMessage(e, "사진을 처리하지 못했어요.")) }
                    catch (e: Exception) { notice(userMessage(e, "색감을 적용하지 못했어요. 다른 스타일을 선택해 주세요.")) }
                }
        }
    }

    private fun setStage(stage: WorkStage) { mutableStage.value = stage; mutableBusy.value = stage != WorkStage.IDLE }

    fun update(transform: (Preferences) -> Preferences) {
        if (mutableBusy.value) return
        val next = transform(mutablePreferences.value)
        if (next == mutablePreferences.value) return
        if (next.grade != mutablePreferences.value.grade) mutableGradeReady.value = false
        mutablePreferences.value = next
        storage.write(next)
    }

    fun selectLook(id: String) {
        if (mutableBusy.value || id == mutablePreferences.value.grade.lookId) return
        // Also migrate an untouched selection from older versions before switching away.
        storage.write(mutablePreferences.value)
        update { it.copy(grade = storage.readGrade(id)) }
    }
    fun notice(message: String) { mutableMessage.tryEmit(message) }

    fun capture(engine: CameraEngine) {
        if (mutableBusy.value || !mutableGradeReady.value || !engine.status.value.ready) return
        val settings = mutablePreferences.value
        val snapshotLut = mutableLut.value
        setStage(if (settings.timer > 0) WorkStage.COUNTDOWN else WorkStage.CAPTURING)
        captureJob = viewModelScope.launch {
            var source: File? = null
            try {
                for (second in settings.timer downTo 1) { mutableCountdown.value = second; delay(1000) }
                mutableCountdown.value = 0
                setStage(WorkStage.CAPTURING)
                val captured = engine.takePicture(engine.status.value.frontFacing && settings.mirrorSelfie)
                source = captured.file
                setStage(WorkStage.SAVING)
                val result = photos.export(source, snapshotLut, settings.grade, settings.saveOriginal, captured.frame)
                mutableLatest.value = result.uri; storage.latestUri = result.uri.toString()
                notice(if (result.original != null) "원본과 보정한 사진을 갤러리에 저장했어요." else "보정한 사진을 갤러리에 저장했어요.")
            } catch (cancel: CancellationException) { throw cancel }
            catch (e: OutOfMemoryError) { notice(userMessage(e, "사진을 처리하지 못했어요.")) }
            catch (e: Exception) { notice(userMessage(e, "사진을 저장하지 못했어요. 저장 공간을 확인하고 다시 촬영해 주세요.")) }
            finally { source?.delete(); mutableCountdown.value = 0; setStage(WorkStage.IDLE) }
        }
    }

    fun cancelTimer() { if (mutableStage.value == WorkStage.COUNTDOWN) { captureJob?.cancel(); mutableCountdown.value = 0 } }

    fun importPhoto(uri: Uri) {
        if (mutableBusy.value) return
        setStage(WorkStage.IMPORTING)
        importJob = viewModelScope.launch {
            var file: File? = null
            try {
                file = photos.copyImport(uri)
                val original = photos.thumbnail(file)
                mutableImported.value?.delete()
                mutableGradeReady.value = false
                mutableImportPreview.value = null
                mutableImported.value = file
                mutableImportOriginal.value = original
            } catch (cancel: CancellationException) { file?.delete(); throw cancel }
            catch (e: OutOfMemoryError) { file?.delete(); notice(userMessage(e, "사진을 불러오지 못했어요.")) }
            catch (e: Exception) { file?.delete(); notice(userMessage(e, "사진을 불러오지 못했어요. 다른 사진을 선택해 주세요.")) }
            finally { setStage(WorkStage.IDLE) }
        }
    }

    fun closeImport() {
        if (mutableBusy.value) return
        mutableImported.value?.delete(); mutableImported.value = null
        mutableImportOriginal.value = null; mutableImportPreview.value = null
    }

    fun saveImport() {
        val source = mutableImported.value ?: return
        if (mutableBusy.value || !mutableGradeReady.value) return
        val settings = mutablePreferences.value
        val snapshotLut = mutableLut.value
        setStage(WorkStage.SAVING)
        viewModelScope.launch {
            try {
                val result = photos.export(source, snapshotLut, settings.grade, false)
                mutableLatest.value = result.uri; storage.latestUri = result.uri.toString()
                setStage(WorkStage.IDLE)
                closeImport()
                notice("편집한 사진을 갤러리에 저장했어요.")
            } catch (cancel: CancellationException) { throw cancel }
            catch (e: OutOfMemoryError) { notice(userMessage(e, "사진을 처리하지 못했어요.")) }
            catch (e: Exception) { notice(userMessage(e, "사진을 저장하지 못했어요. 저장 공간을 확인한 뒤 다시 시도해 주세요.")) }
            finally { setStage(WorkStage.IDLE) }
        }
    }

    suspend fun thumbnail(uri: Uri) = photos.thumbnail(uri)
    fun forgetMissingPhoto(uri: Uri) {
        if (mutableLatest.value == uri) { mutableLatest.value = null; storage.latestUri = null }
    }

    override fun onCleared() {
        captureJob?.cancel(); importJob?.cancel()
        mutableImported.value?.delete()
        super.onCleared()
    }
}
