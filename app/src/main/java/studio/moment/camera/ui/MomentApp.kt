package studio.moment.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import studio.moment.camera.MomentViewModel
import studio.moment.camera.MainActivity
import studio.moment.camera.camera.CameraEngine
import studio.moment.camera.camera.CaptureMode
import studio.moment.camera.camera.FrameRatio
import studio.moment.camera.camera.FocusState
import studio.moment.camera.color.Looks
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

internal enum class Sheet { LOOKS, TUNE, SETTINGS, MODES, ABOUT }

@Composable
fun MomentApp(model: MomentViewModel) = MomentTheme {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val hostView = LocalView.current
    val textScale = LocalDensity.current.fontScale.coerceIn(1f, 2f)
    val prefs by model.preferences.collectAsStateWithLifecycle()
    val lut by model.lut.collectAsStateWithLifecycle()
    val gradeReady by model.gradeReady.collectAsStateWithLifecycle()
    val busy by model.busy.collectAsStateWithLifecycle()
    val stage by model.stage.collectAsStateWithLifecycle()
    val countdown by model.countdown.collectAsStateWithLifecycle()
    val latest by model.latest.collectAsStateWithLifecycle()
    val imported by model.imported.collectAsStateWithLifecycle()
    val importedPreview by model.importPreview.collectAsStateWithLifecycle()
    val importedOriginal by model.importOriginal.collectAsStateWithLifecycle()
    val engine = remember(context) { CameraEngine(context, model::notice) }
    val status by engine.status.collectAsStateWithLifecycle()
    val preview = remember(context) { PreviewView(context).apply {
        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        scaleType = PreviewView.ScaleType.FILL_CENTER
        keepScreenOn = true
    } }
    val snack = remember { SnackbarHostState() }
    var sheet by remember { mutableStateOf<Sheet?>(null) }
    var front by rememberSaveable { mutableStateOf(false) }
    var mode by rememberSaveable { mutableStateOf(CaptureMode.AUTO) }
    var flash by rememberSaveable { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var retry by remember { mutableIntStateOf(0) }
    var comparing by remember { mutableStateOf(false) }
    var focus by remember { mutableStateOf<Offset?>(null) }
    var photoOpen by remember { mutableStateOf(false) }
    var latestBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var latestRefresh by remember { mutableIntStateOf(0) }
    var permission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    var requestedPermission by rememberSaveable { mutableStateOf(false) }
    var permissionDenied by rememberSaveable { mutableStateOf(false) }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permission = granted; permissionDenied = !granted
        if (!granted) model.notice("카메라 권한 없이도 사진을 가져와 색감을 바꿀 수 있어요.")
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri -> uri?.let(model::importPhoto) }
    var pendingSave by remember { mutableStateOf<(() -> Unit)?>(null) }
    val legacyStorage = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) pendingSave?.invoke() else model.notice("사진을 갤러리에 저장하려면 저장소 권한이 필요해요.")
        pendingSave = null
    }
    fun save(action: () -> Unit) {
        if (Build.VERSION.SDK_INT <= 28 && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            pendingSave = action; legacyStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else action()
    }
    fun requestCamera() {
        val activity = context as? Activity
        if (requestedPermission && permissionDenied && activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
        } else { requestedPermission = true; cameraPermission.launch(Manifest.permission.CAMERA) }
    }

    fun triggerShutter() {
        if (busy || !gradeReady || (imported == null && !status.ready)) return
        comparing = false
        engine.setGrade(lut, false)
        if (prefs.haptics) hostView.performHapticFeedback(if (Build.VERSION.SDK_INT >= 30) HapticFeedbackConstants.CONFIRM else HapticFeedbackConstants.VIRTUAL_KEY)
        save { if (imported != null) model.saveImport() else model.capture(engine) }
    }

    val volumeAction by rememberUpdatedState(newValue = {
        if (!prefs.volumeShutter || imported != null || sheet != null || photoOpen || !permission ||
            !owner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) false
        else if (countdown > 0) { model.cancelTimer(); true }
        else if (status.ready) { triggerShutter(); true }
        else false
    })
    DisposableEffect(context) {
        val activity = context as? MainActivity
        activity?.onVolumeShutter = { volumeAction() }
        onDispose { activity?.onVolumeShutter = null }
    }

    LaunchedEffect(Unit) { model.messages.collect { snack.showSnackbar(it) } }
    LaunchedEffect(lut, comparing) { engine.setGrade(lut, comparing) }
    LaunchedEffect(permission, front, mode, prefs.ratio, imported, retry) {
        if (permission && imported == null) engine.bind(owner, preview, front, mode, prefs.ratio) else engine.stop()
    }
    LaunchedEffect(flash, status.ready, status.hasFlash) { engine.setFlash(flash) }
    LaunchedEffect(focus) { if (focus != null) { delay(1300); focus = null } }
    LaunchedEffect(latest, latestRefresh) {
        val uri = latest
        latestBitmap = uri?.let { model.thumbnail(it) }
        if (uri != null && latestBitmap == null) { model.forgetMissingPhoto(uri); photoOpen = false }
    }
    DisposableEffect(owner, engine) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                latestRefresh++
            }
            if (event == Lifecycle.Event.ON_STOP) { model.cancelTimer(); comparing = false }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); engine.close() }
    }
    BackHandler(countdown > 0 || imported != null) {
        if (countdown > 0) model.cancelTimer() else model.closeImport()
    }

    Surface(color = Ink, modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding()) {
            val contentHeight = maxOf(maxHeight, 680.dp * textScale)
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState(), enabled = maxHeight < contentHeight).height(contentHeight)) {
                Row(Modifier.fillMaxWidth().height(62.dp * textScale).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text("모먼트", fontFamily = FontFamily.Serif, fontSize = 30.sp, letterSpacing = (-.5).sp, color = Cream)
                        Box(Modifier.padding(start = 5.dp, bottom = 12.dp).size(6.dp).background(Lime, CircleShape))
                    }
                    ToolIcon(Icons.Outlined.AddPhotoAlternate, "사진 가져오기", enabled = !busy) {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    ToolIcon(Icons.Outlined.Tune, "설정", enabled = !busy) { sheet = Sheet.SETTINGS }
                }
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                    val frameWidth = minOf(maxWidth, maxHeight * prefs.ratio.value)
                    Box(Modifier.width(frameWidth).aspectRatio(prefs.ratio.value).clip(RoundedCornerShape(22.dp)).background(Panel)) {
                        when {
                            imported != null -> {
                                val bitmap = if (comparing) importedOriginal else importedPreview ?: importedOriginal
                                bitmap?.let { Image(it.asImageBitmap(), "가져온 사진 미리보기", Modifier.fillMaxSize(), contentScale = ContentScale.Fit) }
                            }
                            permission -> {
                                AndroidView(factory = { preview }, modifier = Modifier.fillMaxSize())
                                Box(Modifier.fillMaxSize()
                                    .pointerInput(status.ready, busy) {
                                        detectTapGestures(
                                            onTap = { point -> if (status.ready && !busy) { engine.focus(preview, point.x, point.y); focus = point } },
                                            onLongPress = { point -> if (status.ready && !busy) { engine.focus(preview, point.x, point.y, lock = true); focus = point } },
                                        )
                                    }
                                    .pointerInput(status.ready, busy) {
                                        detectTransformGestures { _, _, zoom, _ -> if (status.ready && !busy) engine.zoom(engine.status.value.zoom * zoom) }
                                    })
                                if (prefs.grid && status.ready) GridOverlay()
                                focus?.let { point -> Canvas(Modifier.fillMaxSize()) {
                                    val side = 66.dp.toPx()
                                    drawRoundRect(if (status.focus == FocusState.FAILED) Color(0xFFF2A99D) else if (status.focus == FocusState.FOCUSING) Cream else Lime,
                                        Offset(point.x - side / 2, point.y - side / 2), Size(side, side),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f), style = Stroke(1.5.dp.toPx()))
                                } }
                                if (!status.ready && status.error == null) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Lime, modifier = Modifier.size(28.dp), strokeWidth = 2.dp)
                                }
                                status.error?.let { error -> CameraMessage("카메라를 열지 못했어요", error, "다시 시도", { retry++ }) }
                            }
                            else -> PermissionScene(permissionDenied) { requestCamera() }
                        }
                        if (status.focus == FocusState.LOCKED && imported == null) TextButton(
                            onClick = engine::unlockFocus, enabled = !busy,
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 52.dp).background(Color.Black.copy(alpha = .5f), RoundedCornerShape(50)),
                        ) { Icon(Icons.Outlined.Lock, null, Modifier.size(14.dp)); Text(" 초점 고정 해제", fontSize = 11.sp) }
                        if (imported != null && !gradeReady) Box(Modifier.align(Alignment.TopEnd).padding(12.dp)
                            .background(Color.Black.copy(alpha = .65f), RoundedCornerShape(12.dp)).padding(10.dp)) {
                            Text("색감을 적용하는 중이에요", color = Cream, fontSize = 11.sp)
                        }
                        if (permission && imported == null && status.ready) {
                            Row(Modifier.align(Alignment.TopCenter).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .5f), Color.Transparent))).padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                GlassIcon(when (flash) { ImageCapture.FLASH_MODE_ON -> Icons.Outlined.FlashOn; ImageCapture.FLASH_MODE_AUTO -> Icons.Outlined.FlashAuto; else -> Icons.Outlined.FlashOff },
                                    "플래시 ${when (flash) { ImageCapture.FLASH_MODE_ON -> "켜짐"; ImageCapture.FLASH_MODE_AUTO -> "자동"; else -> "꺼짐" }}", status.hasFlash && !busy, flash != ImageCapture.FLASH_MODE_OFF) {
                                    flash = when (flash) { ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_AUTO; ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON; else -> ImageCapture.FLASH_MODE_OFF }
                                }
                                GlassText(prefs.ratio.label, "사진 비율", !busy) { model.update { it.copy(ratio = FrameRatio.entries[(it.ratio.ordinal + 1) % FrameRatio.entries.size]) } }
                                GlassText(if (prefs.timer == 0) "타이머" else "${prefs.timer}초", "타이머", !busy) {
                                    model.update { it.copy(timer = when (it.timer) { 0 -> 3; 3 -> 10; else -> 0 }) }
                                }
                            }
                        }
                        if (imported != null) IconButton(onClick = model::closeImport, enabled = !busy, modifier = Modifier.align(Alignment.TopStart).padding(6.dp).background(Color.Black.copy(alpha = .4f), CircleShape)) {
                            Icon(Icons.Outlined.Close, "가져온 사진 닫기", tint = Cream)
                        }
                        if (status.ready || imported != null) {
                            Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .55f)))).padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (comparing) "원본" else "아이폰 ${Looks.find(prefs.grade.lookId).model} 스타일", color = Cream,
                                    fontSize = 12.sp, letterSpacing = .7.sp, modifier = Modifier.padding(start = 4.dp))
                                CompareButton(onCompare = { comparing = it })
                            }
                        }
                        if (countdown > 0) Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .18f)).clickable(onClick = model::cancelTimer), contentAlignment = Alignment.Center) {
                            Text(countdown.toString(), fontFamily = FontFamily.Serif, fontSize = 100.sp, color = Cream)
                            Text("탭하여 취소", color = Cream, fontSize = 12.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().height(45.dp * textScale).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    if (imported == null) {
                        TextButton(onClick = { sheet = Sheet.MODES }, enabled = permission && !busy, contentPadding = PaddingValues(horizontal = 8.dp)) {
                            Box(Modifier.size(5.dp).background(if (status.ready) Lime else Muted, CircleShape))
                            Text("  ${status.activeMode.label}", fontSize = 12.sp, color = Cream)
                            Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp), tint = Muted)
                        }
                        Row(Modifier.weight(1f).padding(horizontal = 4.dp).horizontalScroll(rememberScrollState()), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val zooms = (listOf(status.minZoom).filter { it < .95f } + listOf(1f, 2f, 3f, 5f).filter { it >= status.minZoom && it <= status.maxZoom }).distinct()
                            zooms.take(5).forEach { zoom ->
                                val selected = abs(status.zoom - zoom) < .08f
                                TextButton(onClick = { engine.zoom(zoom) }, enabled = status.ready && !busy,
                                    modifier = Modifier.height(36.dp).widthIn(min = 40.dp), contentPadding = PaddingValues(horizontal = 7.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = if (selected) Lime else Muted)) {
                                    Text("${formatZoom(zoom)}배", fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                                }
                            }
                        }
                    } else Text("가져온 사진 편집", color = Muted, fontSize = 12.sp)
                    Text(if (imported == null && status.ready) "${formatZoom(status.zoom)}배" else "강도 ${(prefs.grade.intensity * 100).roundToInt()}%", color = Muted, fontSize = 11.sp)
                }
                LookStrip(prefs.grade.lookId, !busy, model::selectLook)
                Row(Modifier.fillMaxWidth().heightIn(min = 40.dp * textScale).padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { sheet = Sheet.LOOKS }, enabled = !busy, contentPadding = PaddingValues(0.dp), modifier = Modifier.weight(1f)) {
                        Text("${Looks.find(prefs.grade.lookId).title}  ", color = Muted, fontSize = 12.sp, modifier = Modifier.weight(1f, fill = false))
                        Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp), tint = Muted)
                    }
                    TextButton(onClick = { sheet = Sheet.TUNE }, enabled = !busy, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Outlined.Tune, null, Modifier.size(15.dp), tint = Lime)
                        Text("  색감 조절", color = Lime, fontSize = 12.sp)
                    }
                }
                Row(Modifier.fillMaxWidth().height(96.dp * textScale).padding(horizontal = 28.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).clip(RoundedCornerShape(13.dp)).background(Panel).border(1.dp, Line, RoundedCornerShape(13.dp))
                        .clickable(enabled = !busy, role = Role.Button, onClick = {
                            if (latest != null) photoOpen = true else picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        }).semantics { contentDescription = if (latest != null) "최근 사진" else "사진 가져오기" }, contentAlignment = Alignment.Center) {
                        if (latestBitmap != null) Image(latestBitmap!!.asImageBitmap(), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        else Icon(Icons.Outlined.PhotoLibrary, null, tint = Muted)
                    }
                    Shutter(enabled = !busy && gradeReady && (status.ready || imported != null), busy = busy, countdown = countdown,
                        importing = imported != null, onClick = ::triggerShutter)
                    ToolIcon(if (imported != null) Icons.Outlined.Close else Icons.Outlined.Cameraswitch,
                        if (imported != null) "사진 편집 닫기" else "카메라 전환", enabled = !busy && (imported != null || status.hasFront && status.hasBack)) {
                        if (imported != null) model.closeImport() else front = !status.frontFacing
                    }
                }
                Text(if (busy) stage.label else if (imported != null) "저장하면 새 사진이 만들어져요" else "화면을 길게 누르면 초점을 고정할 수 있어요", color = Muted,
                    fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp))
            }
            SnackbarHost(snack, Modifier.align(Alignment.BottomCenter).padding(12.dp))
        }
        sheet?.let { selected ->
            MomentSheet(selected, prefs, status, imported != null, onDismiss = { sheet = null }, onUpdate = model::update,
                onLook = model::selectLook, onMode = { mode = it; sheet = null }, onExposure = engine::exposure,
                onAbout = { sheet = Sheet.ABOUT })
        }
        if (photoOpen && latest != null) PhotoDialog(latest!!, latestBitmap, { photoOpen = false }) { intent ->
            runCatching { context.startActivity(intent) }.onFailure { model.notice("사진을 열지 못했어요. 갤러리에서 사진이 삭제되지 않았는지 확인해 주세요.") }
        }
    }
}

@Composable
private fun LookStrip(selected: String, enabled: Boolean, onSelect: (String) -> Unit) {
    val state = rememberLazyListState()
    val textScale = LocalDensity.current.fontScale.coerceIn(1f, 2f)
    LaunchedEffect(selected) { state.animateScrollToItem((Looks.all.indexOfFirst { it.id == selected } - 1).coerceAtLeast(0)) }
    LazyRow(state = state, contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(Looks.all, key = { it.id }) { look ->
            val active = look.id == selected
            Column(Modifier.width((if (look.model.length > 3) 96.dp else 77.dp) * textScale).height(78.dp * textScale).clip(RoundedCornerShape(15.dp))
                .background(if (active) Color(look.swatch) else Panel)
                .border(1.dp, if (active) Color(look.swatch) else Line, RoundedCornerShape(15.dp))
                .clickable(enabled = enabled, role = Role.RadioButton) { onSelect(look.id) }
                .semantics { contentDescription = "아이폰 ${look.model} 스타일${if (active) ", 선택됨" else ""}" }
                .padding(horizontal = 12.dp, vertical = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text("아이폰 스타일", fontSize = 9.sp, maxLines = 1, color = if (active) Ink.copy(alpha = .65f) else Muted)
                Text(look.model, fontFamily = FontFamily.Serif, fontSize = if (look.model.length > 3) 24.sp else 29.sp,
                    color = if (active) Ink else Cream, letterSpacing = (-.7).sp)
            }
        }
    }
}

@Composable
private fun Shutter(enabled: Boolean, busy: Boolean, countdown: Int, importing: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(80.dp).border(1.5.dp, if (enabled || busy) Cream else Line, CircleShape).padding(6.dp)
        .clip(CircleShape).background(if (enabled) Cream else Panel).clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        .testTag("shutter").semantics { contentDescription = if (importing) "편집한 사진 저장" else "사진 촬영" }, contentAlignment = Alignment.Center) {
        when {
            busy && countdown == 0 -> CircularProgressIndicator(color = Lime, modifier = Modifier.size(25.dp), strokeWidth = 2.dp)
            importing -> Icon(Icons.Outlined.Check, null, tint = if (enabled) Ink else Muted, modifier = Modifier.size(29.dp))
            else -> Box(Modifier.size(8.dp).background(if (enabled) Ink else Muted, CircleShape))
        }
    }
}

@Composable
internal fun ToolIcon(icon: ImageVector, label: String, enabled: Boolean = true, onClick: () -> Unit) {
    IconButton(onClick, enabled = enabled, modifier = Modifier.size(48.dp)) {
        Icon(icon, label, tint = if (enabled) Cream else Muted.copy(alpha = .35f), modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun GlassIcon(icon: ImageVector, label: String, enabled: Boolean, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick, enabled = enabled, modifier = Modifier.size(44.dp)) {
        Icon(icon, label, tint = if (!enabled) Muted else if (selected) Lime else Cream, modifier = Modifier.size(20.dp))
    }
}

@Composable
private fun GlassText(text: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    TextButton(onClick, enabled = enabled, modifier = Modifier.height(44.dp).semantics { contentDescription = if (label == text) label else "$label $text" }) {
        Text(text, color = Cream, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompareButton(onCompare: (Boolean) -> Unit) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    var locked by remember { mutableStateOf(false) }
    LaunchedEffect(pressed, locked) { onCompare(pressed || locked) }
    DisposableEffect(Unit) { onDispose { onCompare(false) } }
    Row(Modifier.heightIn(min = 44.dp).clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = .3f))
        .combinedClickable(interactionSource = interactions, indication = null, role = Role.Button,
            onClick = { locked = !locked }, onLongClick = {}, onLongClickLabel = "누르는 동안 원본 보기")
        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Outlined.Compare, null, Modifier.size(15.dp), tint = if (pressed || locked) Lime else Cream)
        Text(if (locked) "  색감 보기" else "  원본 비교", color = Cream, fontSize = 11.sp)
    }
}

@Composable
private fun GridOverlay() {
    Canvas(Modifier.fillMaxSize()) {
        for (part in 1..2) {
            drawLine(Color.White.copy(alpha = .22f), Offset(size.width * part / 3, 0f), Offset(size.width * part / 3, size.height), .65.dp.toPx())
            drawLine(Color.White.copy(alpha = .22f), Offset(0f, size.height * part / 3), Offset(size.width, size.height * part / 3), .65.dp.toPx())
        }
    }
}

@Composable
private fun PermissionScene(denied: Boolean, onPermission: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF303B2A), Color(0xFF20291E), Color(0xFF171F16))))) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color(0xFFD9CFAD).copy(alpha = .14f), radius = size.width * .28f, center = Offset(size.width * .74f, size.height * .3f))
            drawCircle(Color(0xFF48563D).copy(alpha = .4f), radius = size.width * 1.0f, center = Offset(size.width * .2f, size.height * 1.14f))
            drawCircle(Color(0xFF697055).copy(alpha = .2f), radius = size.width * .8f, center = Offset(size.width * 1.05f, size.height * 1.04f))
        }
        Column(Modifier.align(Alignment.Center).padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.CameraAlt, null, tint = Lime, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(20.dp))
            Text("좋아하는 색감으로\n촬영해 보세요", color = Cream, fontFamily = FontFamily.Serif, fontSize = 25.sp, lineHeight = 36.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Text(if (denied) "카메라 권한을 허용하거나\n위쪽 버튼으로 사진을 가져오세요." else "촬영하려면 카메라 권한이 필요해요.\n사진을 가져와 편집할 수도 있어요.", fontSize = 12.sp, lineHeight = 19.sp, color = Cream.copy(alpha = .8f), textAlign = TextAlign.Center)
            Spacer(Modifier.height(22.dp))
            Button(onClick = onPermission, colors = ButtonDefaults.buttonColors(containerColor = Lime), shape = RoundedCornerShape(50)) {
                Text(if (denied) "카메라 권한 확인" else "카메라 사용하기", color = Ink, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
        }
    }
}

@Composable
private fun CameraMessage(title: String, body: String, action: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Panel).padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.CameraAlt, null, tint = Muted, modifier = Modifier.size(32.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, color = Cream, fontSize = 18.sp)
        Text(body, color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
        TextButton(onClick) { Text(action) }
    }
}

@Composable
private fun PhotoDialog(uri: Uri, bitmap: Bitmap?, onDismiss: () -> Unit, launch: (Intent) -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.clip(RoundedCornerShape(24.dp)).background(Panel).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("최근 저장한 사진", color = Cream, modifier = Modifier.weight(1f))
                ToolIcon(Icons.Outlined.Close, "닫기", onClick = onDismiss)
            }
            bitmap?.let { Image(it.asImageBitmap(), "최근 저장한 사진", Modifier.fillMaxWidth().heightIn(max = 420.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Fit) }
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                TextButton(onClick = { launch(Intent(Intent.ACTION_VIEW).setDataAndType(uri, "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) }) { Text("갤러리에서 열기") }
                TextButton(onClick = { launch(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("image/jpeg").putExtra(Intent.EXTRA_STREAM, uri)
                    .apply { clipData = android.content.ClipData.newRawUri("사진", uri) }.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION), "사진 공유")) }) {
                    Icon(Icons.Outlined.IosShare, null, Modifier.size(17.dp)); Text("  공유")
                }
            }
        }
    }
}

private fun formatZoom(zoom: Float): String = if (zoom % 1f == 0f) zoom.toInt().toString() else String.format(Locale.US, "%.1f", zoom)
