package studio.moment.camera.camera

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import studio.moment.camera.color.ColorLut
import studio.moment.camera.data.UserMessageException
import studio.moment.camera.data.userMessage
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

enum class CaptureMode(val label: String, val extension: Int) {
    STANDARD("사진", ExtensionMode.NONE), AUTO("자동 보정", ExtensionMode.AUTO),
    HDR("HDR", ExtensionMode.HDR), NIGHT("야간", ExtensionMode.NIGHT), PORTRAIT("인물", ExtensionMode.BOKEH)
}

enum class FrameRatio(val label: String, val width: Int, val height: Int) {
    CLASSIC("3:4", 3, 4), WIDE("9:16", 9, 16), SQUARE("1:1", 1, 1);
    val value get() = width.toFloat() / height
}

enum class FocusState { IDLE, FOCUSING, FOCUSED, LOCKED, FAILED }

data class CameraStatus(
    val ready: Boolean = false,
    val hasFront: Boolean = false,
    val hasBack: Boolean = false,
    val frontFacing: Boolean = false,
    val hasFlash: Boolean = false,
    val minZoom: Float = 1f,
    val maxZoom: Float = 1f,
    val zoom: Float = 1f,
    val minEv: Float = 0f,
    val maxEv: Float = 0f,
    val ev: Float = 0f,
    val focus: FocusState = FocusState.IDLE,
    val availableModes: Set<CaptureMode> = setOf(CaptureMode.STANDARD),
    val activeMode: CaptureMode = CaptureMode.STANDARD,
    val error: String? = null,
)

class CameraEngine(private val context: Context, private val onNotice: (String) -> Unit) : AutoCloseable {
    private val main = ContextCompat.getMainExecutor(context)
    private val mutableStatus = MutableStateFlow(CameraStatus())
    val status = mutableStatus.asStateFlow()
    private var processor: LutSurfaceProcessor? = null
    private var currentLut = ColorLut.identity
    private var comparing = false
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var capture: ImageCapture? = null
    private var lastOwner: LifecycleOwner? = null
    private var lastView: PreviewView? = null
    private var generation = 0
    private var focusGeneration = 0
    private var closed = false
    private var captureInFlight = false
    private var captureRotation = Surface.ROTATION_0
    private var previewRotation = Surface.ROTATION_0
    private var captureFrame: CaptureFrame? = null
    private var resumeZoom = 1f
    private var resumeEv = 0f
    private val orientation = object : OrientationEventListener(context) {
        override fun onOrientationChanged(degrees: Int) { setCaptureRotation(CaptureOrientation.fromSensor(degrees, captureRotation)) }
    }
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (orientation.canDetectOrientation()) orientation.enable()
                // CameraControl settings reset when a session closes. Restore the requested values.
                camera?.let { restoreControls(it, resumeZoom, resumeEv) }
            }
            Lifecycle.Event.ON_STOP -> {
                orientation.disable()
                focusGeneration++
                mutableStatus.value = mutableStatus.value.copy(ready = false, focus = FocusState.IDLE)
            }
            else -> Unit
        }
    }

    fun setGrade(lut: ColorLut, compareOriginal: Boolean) {
        currentLut = lut; comparing = compareOriginal
        processor?.let { it.lut = lut; it.compareOriginal = compareOriginal }
    }

    fun setCaptureRotation(rotation: Int) {
        if (rotation !in Surface.ROTATION_0..Surface.ROTATION_270) return
        captureRotation = rotation
        if (!captureInFlight) capture?.targetRotation = rotation
    }

    fun setCaptureFrame(frame: CaptureFrame) { captureFrame = frame }

    suspend fun bind(owner: LifecycleOwner, view: PreviewView, front: Boolean, mode: CaptureMode, viewportRatio: android.util.Rational) {
        val previous = mutableStatus.value
        val version = ++generation
        // Immediately prevent capture on a stale session while initialization is suspended.
        capture = null
        mutableStatus.value = previous.copy(ready = false, error = null, focus = FocusState.IDLE)
        try {
            val p = provider ?: ProcessCameraProvider.getInstance(context).await(main).also { provider = it }
            val extensions = try { ExtensionsManager.getInstanceAsync(context, p).await(main) }
                catch (cancel: CancellationException) { throw cancel }
                catch (_: Exception) { null }
            if (version != generation || closed) return
            releaseSession()
            val hasFront = p.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            val hasBack = p.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            if (!hasFront && !hasBack) throw UserMessageException("사용할 수 있는 카메라가 없어요. 사진을 가져와 편집할 수 있어요.")
            val actualFront = if (front) hasFront else !hasBack
            val base = if (actualFront) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            val available = CaptureMode.entries.filter { it == CaptureMode.STANDARD ||
                runCatching { extensions?.isExtensionAvailable(base, it.extension) == true }.getOrDefault(false) }.toMutableSet()
            var active = mode.takeIf { it in available } ?: CaptureMode.STANDARD
            previewRotation = view.display?.rotation ?: Surface.ROTATION_0
            val preview = Preview.Builder().setTargetRotation(previewRotation)
                .setResolutionSelector(ResolutionSelector.Builder().build()).build().apply { surfaceProvider = view.surfaceProvider }
            val still = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setResolutionSelector(ResolutionSelector.Builder().setResolutionStrategy(
                    ResolutionStrategy(android.util.Size(4032, 3024), ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER)
                ).build()).setJpegQuality(96).setTargetRotation(captureRotation).build()
            // A failed GPU processor cannot be reused; each new session gets a fresh one.
            val nextProcessor = LutSurfaceProcessor { error -> main.execute {
                if (version == generation && !closed) mutableStatus.value = mutableStatus.value.copy(ready = false,
                    error = userMessage(error, "미리보기를 표시하지 못했어요. 다시 시도해 주세요."))
            } }.also { it.lut = currentLut; it.compareOriginal = comparing; processor = it }
            val group = UseCaseGroup.Builder().addUseCase(preview).addUseCase(still)
                .setViewPort(ViewPort.Builder(viewportRatio, previewRotation)
                    .setScaleType(ViewPort.FILL_CENTER).build()).addEffect(nextProcessor.effect).build()
            val selector = if (active == CaptureMode.STANDARD) base else extensions!!.getExtensionEnabledCameraSelector(base, active.extension)
            val bound = try { p.bindToLifecycle(owner, selector, group) }
                catch (e: Exception) {
                    if (active == CaptureMode.STANDARD) throw e
                    available.remove(active)
                    p.unbindAll()
                    active = CaptureMode.STANDARD
                    onNotice("선택한 모드를 사용할 수 없어 일반 사진 촬영으로 전환했어요.")
                    p.bindToLifecycle(owner, base, group)
                }
            camera = bound; capture = still; lastOwner = owner; lastView = view
            val info = bound.cameraInfo
            val exposure = info.exposureState
            val step = exposure.exposureCompensationStep.toFloat()
            mutableStatus.value = CameraStatus(hasFront = hasFront, hasBack = hasBack, frontFacing = actualFront,
                hasFlash = info.hasFlashUnit(), minZoom = info.zoomState.value?.minZoomRatio ?: 1f,
                maxZoom = info.zoomState.value?.maxZoomRatio ?: 1f, availableModes = available, activeMode = active,
                minEv = exposure.exposureCompensationRange.lower * step, maxEv = exposure.exposureCompensationRange.upper * step)
            info.zoomState.observe(owner) { zoom -> if (version == generation) {
                mutableStatus.value = mutableStatus.value.copy(zoom = zoom.zoomRatio, minZoom = zoom.minZoomRatio, maxZoom = zoom.maxZoomRatio)
            } }
            // PreviewView can still report STREAMING for the previous surface just after
            // rebinding. Wait for its idle transition before enabling the new shutter.
            var sawPreviewIdle = view.previewStreamState.value == PreviewView.StreamState.IDLE
            var streaming = false
            info.cameraState.observe(owner) { state -> if (version == generation) {
                if (state.error != null) mutableStatus.value = mutableStatus.value.copy(ready = false,
                    error = "카메라를 사용할 수 없어요. 다른 카메라 앱을 닫은 뒤 다시 시도해 주세요.")
                else mutableStatus.value = mutableStatus.value.copy(ready = streaming && state.type == CameraState.Type.OPEN && mutableStatus.value.error == null)
            } }
            view.previewStreamState.observe(owner) { stream -> if (version == generation) {
                if (stream == PreviewView.StreamState.IDLE) sawPreviewIdle = true
                streaming = sawPreviewIdle && stream == PreviewView.StreamState.STREAMING
                mutableStatus.value = mutableStatus.value.copy(ready = streaming && info.cameraState.value?.type == CameraState.Type.OPEN && mutableStatus.value.error == null)
            } }
            resumeZoom = if (previous.frontFacing == actualFront) previous.zoom else 1f
            resumeEv = if (previous.frontFacing == actualFront) previous.ev else 0f
            owner.lifecycle.addObserver(lifecycleObserver)
            restoreControls(bound, resumeZoom, resumeEv)
            if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED) && orientation.canDetectOrientation()) orientation.enable()
        } catch (cancel: CancellationException) { throw cancel }
        catch (e: Exception) {
            if (version == generation && !closed) {
                releaseSession()
                mutableStatus.value = mutableStatus.value.copy(ready = false,
                    error = userMessage(e, "카메라를 시작하지 못했어요. 잠시 후 다시 시도해 주세요."))
            }
        }
    }

    private fun restoreControls(bound: Camera, zoom: Float, ev: Float) {
        if (camera !== bound) return
        zoom(zoom)
        exposure(ev)
    }

    fun setFlash(mode: Int) { capture?.flashMode = if (mutableStatus.value.hasFlash) mode else ImageCapture.FLASH_MODE_OFF }

    fun zoom(ratio: Float) {
        if (!ratio.isFinite()) return
        val c = camera ?: return
        val requested = ratio.coerceIn(mutableStatus.value.minZoom, mutableStatus.value.maxZoom)
        resumeZoom = requested
        val future = c.cameraControl.setZoomRatio(requested)
        future.addListener({ runCatching { future.get() }.onFailure {
            if (camera === c && mutableStatus.value.ready) onNotice("이 카메라에서는 요청한 배율을 사용할 수 없어요.")
        } }, main)
    }

    fun focus(view: PreviewView, x: Float, y: Float, lock: Boolean = false) {
        val c = camera ?: return
        if (!mutableStatus.value.ready) return
        val request = ++focusGeneration
        val point = view.meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE).apply {
            if (lock) disableAutoCancel() else setAutoCancelDuration(4, TimeUnit.SECONDS)
        }.build()
        // A focus lock must have AF support; an AE-only metering success is not an AF lock.
        val af = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
        if (!c.cameraInfo.isFocusMeteringSupported(action) || (lock && !c.cameraInfo.isFocusMeteringSupported(af))) {
            mutableStatus.value = mutableStatus.value.copy(focus = FocusState.FAILED)
            onNotice(if (lock) "이 카메라에서는 초점을 고정할 수 없어요." else "이 카메라에서는 터치 초점을 사용할 수 없어요.")
            return
        }
        mutableStatus.value = mutableStatus.value.copy(focus = FocusState.FOCUSING)
        val future = c.cameraControl.startFocusAndMetering(action)
        future.addListener({
            if (camera !== c || request != focusGeneration) return@addListener
            val result = runCatching { future.get() }.getOrNull()
            mutableStatus.value = mutableStatus.value.copy(focus = when {
                result?.isFocusSuccessful != true -> FocusState.FAILED
                lock -> FocusState.LOCKED
                else -> FocusState.FOCUSED
            })
            if (lock && result?.isFocusSuccessful != true) onNotice("초점을 고정하지 못했어요. 다른 위치를 길게 눌러 주세요.")
        }, main)
    }

    fun unlockFocus() {
        focusGeneration++
        camera?.cameraControl?.cancelFocusAndMetering()
        mutableStatus.value = mutableStatus.value.copy(focus = FocusState.IDLE)
    }

    fun exposure(ev: Float) {
        val c = camera ?: return
        val state = c.cameraInfo.exposureState
        val step = state.exposureCompensationStep.toFloat()
        if (!ev.isFinite() || !state.isExposureCompensationSupported || step <= 0f) return
        val index = (ev / step).roundToInt().coerceIn(state.exposureCompensationRange.lower, state.exposureCompensationRange.upper)
        resumeEv = index * step
        val future = c.cameraControl.setExposureCompensationIndex(index)
        future.addListener({ runCatching { future.get() }.onSuccess {
            if (camera === c) mutableStatus.value = mutableStatus.value.copy(ev = index * step)
        }.onFailure { if (camera === c && mutableStatus.value.ready) onNotice("노출을 변경하지 못했어요. 다시 시도해 주세요.") } }, main)
    }

    suspend fun takePicture(mirror: Boolean): CapturedPhoto = suspendCancellableCoroutine { continuation ->
        val still = capture
        val bound = camera
        val frame = captureFrame
        if (still == null || bound == null || frame == null || !mutableStatus.value.ready || captureInFlight) {
            continuation.resumeWithException(UserMessageException("카메라가 준비된 뒤 다시 촬영해 주세요."))
            return@suspendCancellableCoroutine
        }
        val folder = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File.createTempFile("capture-", ".jpg", folder)
        captureInFlight = true
        still.targetRotation = captureRotation
        val rotationDelta = bound.cameraInfo.getSensorRotationDegrees(captureRotation) - bound.cameraInfo.getSensorRotationDegrees(previewRotation)
        // PreviewView mirrors the front camera; JPEG mirroring is a separate user setting.
        val unmirroredFrame = if (mutableStatus.value.frontFacing) frame.mirrorHorizontally() else frame
        val rotatedFrame = unmirroredFrame.rotateClockwise(rotationDelta / 90)
        val savedFrame = if (mirror) rotatedFrame.mirrorHorizontally() else rotatedFrame
        val metadata = ImageCapture.Metadata().apply { isReversedHorizontal = mirror }
        val options = ImageCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build()
        fun finished() { captureInFlight = false; capture?.targetRotation = captureRotation }
        try {
            still.takePicture(options, main, object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                    finished()
                    // Delete the file if cancellation wins the dispatcher hand-off race.
                    if (continuation.isActive) continuation.resume(CapturedPhoto(file, savedFrame)) { _, value, _ -> value.file.delete() } else file.delete()
                }
                override fun onError(exception: ImageCaptureException) {
                    finished(); file.delete()
                    if (continuation.isActive) continuation.resumeWithException(exception)
                }
            })
        } catch (e: Exception) { finished(); file.delete(); if (continuation.isActive) continuation.resumeWithException(e) }
    }

    private fun releaseSession() {
        orientation.disable()
        focusGeneration++
        lastOwner?.let { owner ->
            owner.lifecycle.removeObserver(lifecycleObserver)
            camera?.cameraInfo?.zoomState?.removeObservers(owner)
            camera?.cameraInfo?.cameraState?.removeObservers(owner)
            lastView?.previewStreamState?.removeObservers(owner)
        }
        lastOwner = null; lastView = null
        provider?.unbindAll()
        camera = null; capture = null
        processor?.close(); processor = null
    }

    fun stop() { generation++; releaseSession(); mutableStatus.value = mutableStatus.value.copy(ready = false, focus = FocusState.IDLE) }
    override fun close() { closed = true; stop() }
}

private suspend fun <T> ListenableFuture<T>.await(executor: java.util.concurrent.Executor): T = suspendCancellableCoroutine { c ->
    addListener({ if (c.isActive) runCatching { get() }.fold(c::resume, c::resumeWithException) }, executor)
}
