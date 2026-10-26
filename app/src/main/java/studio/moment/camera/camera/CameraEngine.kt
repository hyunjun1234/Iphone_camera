package studio.moment.camera.camera

import android.content.Context
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
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.math.roundToInt

enum class CaptureMode(val label: String, val extension: Int) {
    STANDARD("일반", ExtensionMode.NONE), AUTO("자동", ExtensionMode.AUTO),
    HDR("HDR", ExtensionMode.HDR), NIGHT("야간", ExtensionMode.NIGHT), PORTRAIT("인물", ExtensionMode.BOKEH)
}

enum class FrameRatio(val label: String, val width: Int, val height: Int) {
    CLASSIC("3:4", 3, 4), WIDE("9:16", 9, 16), SQUARE("1:1", 1, 1);
    val value get() = width.toFloat() / height
}

data class CameraStatus(
    val ready: Boolean = false,
    val hasFront: Boolean = false,
    val hasBack: Boolean = false,
    val hasFlash: Boolean = false,
    val minZoom: Float = 1f,
    val maxZoom: Float = 1f,
    val zoom: Float = 1f,
    val minEv: Float = 0f,
    val maxEv: Float = 0f,
    val ev: Float = 0f,
    val availableModes: Set<CaptureMode> = setOf(CaptureMode.STANDARD),
    val activeMode: CaptureMode = CaptureMode.STANDARD,
    val error: String? = null,
)

class CameraEngine(private val context: Context, private val onNotice: (String) -> Unit) : AutoCloseable {
    private val main = ContextCompat.getMainExecutor(context)
    private val mutableStatus = MutableStateFlow(CameraStatus())
    val status = mutableStatus.asStateFlow()
    val processor = LutSurfaceProcessor {
        main.execute { mutableStatus.value = mutableStatus.value.copy(ready = false, error = "색감 미리보기를 시작하지 못했어요. 카메라를 다시 열어주세요.") }
    }
    private var provider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var capture: ImageCapture? = null
    private var lastOwner: LifecycleOwner? = null
    private var lastView: PreviewView? = null
    private var generation = 0
    private var closed = false

    suspend fun bind(owner: LifecycleOwner, view: PreviewView, front: Boolean, mode: CaptureMode, ratio: FrameRatio) {
        val version = ++generation
        mutableStatus.value = mutableStatus.value.copy(ready = false, error = null)
        try {
            val p = provider ?: ProcessCameraProvider.getInstance(context).await(main).also { provider = it }
            val extensions = ExtensionsManager.getInstanceAsync(context, p).await(main)
            if (version != generation || closed) return
            unbindObservers()
            p.unbindAll()
            val baseSelector = if (front) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            check(p.hasCamera(baseSelector)) { "이 방향의 카메라가 없습니다." }
            val available = CaptureMode.entries.filter { it == CaptureMode.STANDARD ||
                runCatching { extensions.isExtensionAvailable(baseSelector, it.extension) }.getOrDefault(false) }.toSet()
            var active = if (mode in available) mode else CaptureMode.STANDARD
            val preview = Preview.Builder().setResolutionSelector(ResolutionSelector.Builder().build()).build()
            preview.surfaceProvider = view.surfaceProvider
            val still = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .setResolutionSelector(ResolutionSelector.Builder().setResolutionStrategy(
                    ResolutionStrategy(android.util.Size(4032, 3024), ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER_THEN_HIGHER)
                ).build())
                .setJpegQuality(96)
                .setTargetRotation(Surface.ROTATION_0)
                .build()
            val group = UseCaseGroup.Builder().addUseCase(preview).addUseCase(still)
                .setViewPort(ViewPort.Builder(android.util.Rational(ratio.width, ratio.height), Surface.ROTATION_0)
                    .setScaleType(ViewPort.FILL_CENTER).build())
                .addEffect(processor.effect).build()
            fun selector() = if (active == CaptureMode.STANDARD) baseSelector else
                extensions.getExtensionEnabledCameraSelector(baseSelector, active.extension)
            val bound = try {
                p.bindToLifecycle(owner, selector(), group)
            } catch (e: Exception) {
                if (active == CaptureMode.STANDARD) throw e
                p.unbindAll()
                active = CaptureMode.STANDARD
                onNotice("이 기기에서는 선택한 모드와 색감 미리보기를 함께 쓸 수 없어 일반 촬영으로 전환했어요.")
                p.bindToLifecycle(owner, baseSelector, group)
            }
            camera = bound; capture = still; lastOwner = owner; lastView = view
            val info = bound.cameraInfo
            val exposure = info.exposureState
            val step = exposure.exposureCompensationStep.toFloat()
            mutableStatus.value = CameraStatus(
                hasFront = p.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA),
                hasBack = p.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA),
                hasFlash = info.hasFlashUnit(),
                minZoom = info.zoomState.value?.minZoomRatio ?: 1f,
                maxZoom = info.zoomState.value?.maxZoomRatio ?: 1f,
                availableModes = available,
                activeMode = active,
                minEv = exposure.exposureCompensationRange.lower * step,
                maxEv = exposure.exposureCompensationRange.upper * step,
            )
            info.zoomState.observe(owner) { zoom ->
                mutableStatus.value = mutableStatus.value.copy(zoom = zoom.zoomRatio, minZoom = zoom.minZoomRatio, maxZoom = zoom.maxZoomRatio)
            }
            info.cameraState.observe(owner) { state ->
                if (state.error != null) mutableStatus.value = mutableStatus.value.copy(ready = false, error = "카메라를 사용할 수 없어요. 다른 카메라 앱을 닫고 다시 시도해 주세요.")
                else if (state.type == CameraState.Type.CLOSED || state.type == CameraState.Type.CLOSING)
                    mutableStatus.value = mutableStatus.value.copy(ready = false)
            }
            view.previewStreamState.observe(owner) { stream ->
                mutableStatus.value = mutableStatus.value.copy(ready = stream == PreviewView.StreamState.STREAMING)
            }
        } catch (e: Exception) {
            if (version == generation && !closed) mutableStatus.value = mutableStatus.value.copy(ready = false, error = e.message ?: "카메라를 시작할 수 없어요.")
        }
    }

    fun setFlash(mode: Int) { capture?.flashMode = if (mutableStatus.value.hasFlash) mode else ImageCapture.FLASH_MODE_OFF }

    fun zoom(ratio: Float) {
        camera?.cameraControl?.setZoomRatio(ratio.coerceIn(mutableStatus.value.minZoom, mutableStatus.value.maxZoom))
            ?.addListener({}, main)
    }

    fun focus(view: PreviewView, x: Float, y: Float) {
        val point = view.meteringPointFactory.createPoint(x, y)
        val action = FocusMeteringAction.Builder(point).setAutoCancelDuration(4, TimeUnit.SECONDS).build()
        camera?.cameraControl?.startFocusAndMetering(action)?.addListener({}, main)
    }

    fun exposure(ev: Float) {
        val c = camera ?: return
        val state = c.cameraInfo.exposureState
        val step = state.exposureCompensationStep.toFloat()
        if (!state.isExposureCompensationSupported || step <= 0f) return
        val index = (ev / step).roundToInt().coerceIn(state.exposureCompensationRange.lower, state.exposureCompensationRange.upper)
        val future = c.cameraControl.setExposureCompensationIndex(index)
        future.addListener({ runCatching { future.get() }.onSuccess { mutableStatus.value = mutableStatus.value.copy(ev = index * step) } }, main)
    }

    suspend fun takePicture(mirror: Boolean): File = suspendCancellableCoroutine { continuation ->
        val still = capture
        if (still == null || !mutableStatus.value.ready) {
            continuation.resumeWithException(IllegalStateException("카메라가 준비될 때까지 기다려 주세요."))
            return@suspendCancellableCoroutine
        }
        val folder = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File.createTempFile("capture-", ".jpg", folder)
        val metadata = ImageCapture.Metadata().apply { isReversedHorizontal = mirror }
        val options = ImageCapture.OutputFileOptions.Builder(file).setMetadata(metadata).build()
        still.takePicture(options, main, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                if (continuation.isActive) continuation.resume(file) else file.delete()
            }
            override fun onError(exception: ImageCaptureException) {
                file.delete()
                if (continuation.isActive) continuation.resumeWithException(exception)
            }
        })
    }

    private fun unbindObservers() {
        lastOwner?.let { owner ->
            camera?.cameraInfo?.zoomState?.removeObservers(owner)
            camera?.cameraInfo?.cameraState?.removeObservers(owner)
            lastView?.previewStreamState?.removeObservers(owner)
        }
    }

    fun stop() {
        generation++
        unbindObservers()
        provider?.unbindAll()
        camera = null; capture = null
        mutableStatus.value = mutableStatus.value.copy(ready = false)
    }

    override fun close() { closed = true; stop(); processor.close() }
}

private suspend fun <T> ListenableFuture<T>.await(executor: java.util.concurrent.Executor): T = suspendCancellableCoroutine { c ->
    addListener({ if (c.isActive) runCatching { get() }.fold(c::resume, c::resumeWithException) }, executor)
}
