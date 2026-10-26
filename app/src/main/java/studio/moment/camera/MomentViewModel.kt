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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.moment.camera.camera.CameraEngine
import studio.moment.camera.color.ColorLut
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.data.PhotoStore
import studio.moment.camera.data.PreferenceStore
import studio.moment.camera.data.Preferences
import java.io.File

class MomentViewModel(app: Application) : AndroidViewModel(app) {
    private val storage = PreferenceStore(app)
    private val photos = PhotoStore(app)
    private val mutablePreferences = MutableStateFlow(storage.read())
    val preferences = mutablePreferences.asStateFlow()
    private val mutableLut = MutableStateFlow(ColorLut.create(mutablePreferences.value.grade))
    val lut = mutableLut.asStateFlow()
    private val mutableGradeReady = MutableStateFlow(true)
    val gradeReady = mutableGradeReady.asStateFlow()
    private val mutableBusy = MutableStateFlow(false)
    val busy = mutableBusy.asStateFlow()
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
    private var previewJob: Job? = null

    init {
        viewModelScope.launch {
            mutablePreferences.map { it.grade }.distinctUntilChanged().collectLatest { grade ->
                mutableGradeReady.value = false
                val next = withContext(Dispatchers.Default) { ColorLut.create(grade) }
                mutableLut.value = next
                refreshImportPreview(next)
                mutableGradeReady.value = true
            }
        }
    }

    fun update(transform: (Preferences) -> Preferences) {
        if (mutableBusy.value) return
        val next = transform(mutablePreferences.value)
        if (next.grade != mutablePreferences.value.grade) mutableGradeReady.value = false
        mutablePreferences.value = next
        storage.write(next)
    }

    fun selectLook(id: String) = update { it.copy(grade = GradeSettings(lookId = id)) }
    fun notice(message: String) { mutableMessage.tryEmit(message) }

    fun capture(engine: CameraEngine, front: Boolean) {
        if (mutableBusy.value || !mutableGradeReady.value || !engine.status.value.ready) return
        val settings = mutablePreferences.value
        val snapshotLut = mutableLut.value
        mutableBusy.value = true
        captureJob = viewModelScope.launch {
            var source: File? = null
            try {
                for (second in settings.timer downTo 1) { mutableCountdown.value = second; delay(1000) }
                mutableCountdown.value = 0
                source = engine.takePicture(front && settings.mirrorSelfie)
                val result = photos.export(source, snapshotLut, settings.grade, settings.saveOriginal)
                mutableLatest.value = result.uri; storage.latestUri = result.uri.toString()
                notice("iPhone ${result.model} 스타일로 저장했어요")
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { notice(e.message ?: "사진을 저장하지 못했어요. 다시 시도해 주세요.") }
            finally { source?.delete(); mutableCountdown.value = 0; mutableBusy.value = false }
        }
    }

    fun cancelTimer() { if (mutableCountdown.value > 0) { captureJob?.cancel(); mutableCountdown.value = 0 } }

    fun importPhoto(uri: Uri) {
        if (mutableBusy.value) return
        mutableBusy.value = true
        viewModelScope.launch {
            var file: File? = null
            try {
                file = photos.copyImport(uri)
                val original = photos.thumbnail(file)
                mutableImported.value?.delete()
                mutableImported.value = file
                mutableImportOriginal.value = original
                mutableImportPreview.value = null
                refreshImportPreview(mutableLut.value)
            } catch (e: CancellationException) { file?.delete(); throw e }
            catch (e: Exception) { file?.delete(); notice(e.message ?: "사진을 불러오지 못했어요.") }
            finally { mutableBusy.value = false }
        }
    }

    private fun refreshImportPreview(lut: ColorLut) {
        val original = mutableImportOriginal.value ?: return
        previewJob?.cancel()
        previewJob = viewModelScope.launch {
            val preview = withContext(Dispatchers.Default) {
                val bitmap = original.copy(Bitmap.Config.ARGB_8888, true)
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                lut.apply(pixels)
                bitmap.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                bitmap
            }
            mutableImportPreview.value = preview
        }
    }

    fun closeImport() {
        if (mutableBusy.value) return
        previewJob?.cancel()
        mutableImported.value?.delete(); mutableImported.value = null
        mutableImportOriginal.value = null; mutableImportPreview.value = null
    }

    fun saveImport() {
        val source = mutableImported.value ?: return
        if (mutableBusy.value || !mutableGradeReady.value) return
        val settings = mutablePreferences.value
        val snapshotLut = mutableLut.value
        mutableBusy.value = true
        viewModelScope.launch {
            try {
                val result = photos.export(source, snapshotLut, settings.grade, false)
                mutableLatest.value = result.uri; storage.latestUri = result.uri.toString()
                mutableBusy.value = false
                closeImport()
                notice("iPhone ${result.model} 스타일로 저장했어요. 가져온 원본은 그대로 유지돼요.")
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) { notice(e.message ?: "사진을 저장하지 못했어요.") }
            finally { mutableBusy.value = false }
        }
    }

    suspend fun thumbnail(uri: Uri) = photos.thumbnail(uri)

    override fun onCleared() {
        mutableImported.value?.delete()
        super.onCleared()
    }
}
