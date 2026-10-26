package studio.moment.camera.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.moment.camera.camera.CameraStatus
import studio.moment.camera.camera.CaptureMode
import studio.moment.camera.camera.FrameRatio
import studio.moment.camera.color.GradeSettings
import studio.moment.camera.color.Looks
import studio.moment.camera.color.evidence
import studio.moment.camera.BuildConfig
import studio.moment.camera.data.Preferences
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MomentSheet(
    sheet: Sheet,
    preferences: Preferences,
    camera: CameraStatus,
    importing: Boolean,
    onDismiss: () -> Unit,
    onUpdate: ((Preferences) -> Preferences) -> Unit,
    onLook: (String) -> Unit,
    onMode: (CaptureMode) -> Unit,
    onExposure: (Float) -> Unit,
    onAbout: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Panel, contentColor = Cream,
        scrimColor = if (sheet == Sheet.TUNE) Color.Transparent else Color.Black.copy(alpha = .45f),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Line) }) {
        val sheetView = LocalView.current
        SideEffect {
            (sheetView.parent as? DialogWindowProvider)?.window?.let { window ->
                WindowCompat.getInsetsController(window, sheetView).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
                if (android.os.Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
            }
        }
        val scroll = key(sheet) { rememberScrollState() }
        Column(Modifier.fillMaxWidth().verticalScroll(scroll).padding(horizontal = 24.dp).padding(bottom = 30.dp)) {
            when (sheet) {
                Sheet.LOOKS -> {
                    SheetTitle("스타일 선택", "원하는 색감을 골라 보세요")
                    Text("아이폰 기종에서 영감을 받아 만든 스타일이에요. 실제 기기의 색감을 측정해 재현한 결과는 아니에요.",
                        color = Muted, fontSize = 12.sp, lineHeight = 19.sp, modifier = Modifier.padding(bottom = 8.dp))
                    TextButton(onClick = onAbout, contentPadding = PaddingValues(0.dp)) { Text("스타일 제작 정보", fontSize = 12.sp) }
                    Looks.all.forEach { look ->
                        val selected = look.id == preferences.grade.lookId
                        Row(Modifier.fillMaxWidth().padding(bottom = 9.dp).clip(RoundedCornerShape(17.dp))
                            .background(if (selected) Line else Ink.copy(alpha = .35f)).clickable { onLook(look.id); onDismiss() }
                            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(58.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Color(look.swatch), Color(look.swatch).copy(alpha = .45f)))), contentAlignment = Alignment.Center) {
                                Text(look.model, color = Ink, fontFamily = FontFamily.Serif, fontSize = if (look.model.length > 3) 19.sp else 27.sp)
                            }
                            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("아이폰 ${look.model} 스타일", color = Cream, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                    Text("  ${look.year}", color = Muted, fontSize = 10.sp)
                                }
                                Text(look.title, color = Color(look.swatch), fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                                Text(look.description, color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                            if (selected) Icon(Icons.Outlined.CheckCircle, "선택됨", tint = Lime, modifier = Modifier.size(19.dp))
                        }
                    }
                    Footnote("스타일을 바꾸면 강도와 온도, 톤도 해당 스타일의 기본값으로 돌아가요.")
                }
                Sheet.TUNE -> {
                    var adjustment by remember { mutableIntStateOf(0) }
                    SheetTitle("색감 조절", "아이폰 ${Looks.find(preferences.grade.lookId).model} 스타일")
                    Row(Modifier.fillMaxWidth().padding(bottom = 14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val tabs = listOf("강도", "온도", "톤") + if (!importing && camera.maxEv > camera.minEv) listOf("노출") else emptyList()
                        tabs.forEachIndexed { index, label ->
                            Choice(label, adjustment == index, Modifier.weight(1f)) { adjustment = index }
                        }
                    }
                    when (adjustment) {
                        0 -> AdjustmentSlider("강도", "${(preferences.grade.intensity * 100).roundToInt()}%", preferences.grade.intensity, 0f..1f, "원본", "스타일") { value ->
                            onUpdate { it.copy(grade = it.grade.copy(intensity = value)) }
                        }
                        1 -> AdjustmentSlider("온도", signed(preferences.grade.warmth * 100), preferences.grade.warmth, -1f..1f, "차갑게", "따뜻하게") { value ->
                            onUpdate { it.copy(grade = it.grade.copy(warmth = value)) }
                        }
                        2 -> AdjustmentSlider("톤", signed(preferences.grade.tone * 100), preferences.grade.tone, -1f..1f, "부드럽게", "깊게") { value ->
                            onUpdate { it.copy(grade = it.grade.copy(tone = value)) }
                        }
                        3 -> AdjustmentSlider("노출", String.format(Locale.US, "%+.1f EV", camera.ev), camera.ev.coerceIn(camera.minEv, camera.maxEv), camera.minEv..camera.maxEv, "어둡게", "밝게", onExposure)
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { onUpdate { it.copy(grade = GradeSettings(lookId = it.grade.lookId)) }; if (!importing) onExposure(0f) }) {
                            Icon(Icons.Outlined.RestartAlt, null, Modifier.size(18.dp)); Text("  초기화")
                        }
                        Button(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("완료", modifier = Modifier.padding(horizontal = 12.dp)) }
                    }
                }
                Sheet.MODES -> {
                    SheetTitle("촬영 모드", "현재 카메라가 지원하는 모드를 확인하세요")
                    CaptureMode.entries.forEach { mode ->
                        val available = mode in camera.availableModes
                        val selected = mode == camera.activeMode
                        val description = when (mode) {
                            CaptureMode.STANDARD -> "자동으로 초점과 밝기를 맞춰 촬영해요"
                            CaptureMode.AUTO -> "장면에 맞춰 휴대폰의 추가 보정을 사용해요"
                            CaptureMode.HDR -> "밝은 부분과 어두운 부분을 함께 살려요"
                            CaptureMode.NIGHT -> "어두운 곳에서 여러 장을 합쳐 밝게 촬영해요"
                            CaptureMode.PORTRAIT -> "인물을 중심으로 배경을 흐리게 표현해요"
                        }
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(15.dp))
                            .background(if (selected) Line else Ink.copy(alpha = .3f)).clickable(enabled = available) { onMode(mode) }
                            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(mode.label, fontSize = 16.sp, color = if (available) Cream else Muted.copy(alpha = .45f))
                                Text(if (available) description else "현재 카메라에서 지원하지 않아요", fontSize = 11.sp, color = Muted, modifier = Modifier.padding(top = 5.dp))
                            }
                            if (selected) Icon(Icons.Outlined.CheckCircle, "선택됨", tint = Lime)
                            else if (!available) Text("미지원", color = Muted.copy(alpha = .5f), fontSize = 11.sp)
                        }
                    }
                    Footnote("야간 촬영은 시간이 걸릴 수 있어요. 저장 안내가 나올 때까지 휴대폰을 움직이지 마세요.")
                    Footnote("지원 모드는 휴대폰과 카메라에 따라 달라요. 기본 카메라의 전용 기능으로 촬영한 사진도 가져와 편집할 수 있어요.")
                }
                Sheet.SETTINGS -> {
                    SheetTitle("설정", "촬영과 저장 방식을 선택하세요")
                    SettingSwitch("격자", "구도를 맞추는 안내선을 표시해요", preferences.grid) { value -> onUpdate { it.copy(grid = value) } }
                    SettingSwitch("원본도 저장", "색감을 적용하기 전 사진도 별도로 저장해요", preferences.saveOriginal) { value -> onUpdate { it.copy(saveOriginal = value) } }
                    SettingSwitch("셀피 좌우 반전", "앞면 카메라 사진을 화면에 보이는 방향으로 저장해요", preferences.mirrorSelfie) { value -> onUpdate { it.copy(mirrorSelfie = value) } }
                    SettingSwitch("촬영 버튼 진동", "촬영 버튼을 누를 때 짧게 진동해요", preferences.haptics) { value -> onUpdate { it.copy(haptics = value) } }
                    SettingSwitch("볼륨 버튼으로 촬영", "촬영 화면에서 볼륨 버튼을 셔터로 사용해요", preferences.volumeShutter) { value -> onUpdate { it.copy(volumeShutter = value) } }
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 18.dp))
                    Text("촬영 비율", color = Cream, fontSize = 14.sp)
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FrameRatio.entries.forEach { ratio ->
                            Choice(ratio.label, ratio == preferences.ratio, Modifier.weight(1f)) { onUpdate { it.copy(ratio = ratio) } }
                        }
                    }
                    Text("타이머", color = Cream, fontSize = 14.sp, modifier = Modifier.padding(top = 20.dp))
                    Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(0, 3, 10).forEach { seconds ->
                            Choice(if (seconds == 0) "끄기" else "${seconds}초", seconds == preferences.timer, Modifier.weight(1f)) { onUpdate { it.copy(timer = seconds) } }
                        }
                    }
                    HorizontalDivider(color = Line, modifier = Modifier.padding(vertical = 22.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Lime, CircleShape))
                        Text("  사진은 기기에서 처리해요", color = Cream, fontSize = 13.sp)
                    }
                    Footnote("모먼트는 사진을 서버로 전송하지 않아요. 선택한 스타일과 설정은 다음 실행에도 유지돼요.")
                    TextButton(onClick = onAbout, contentPadding = PaddingValues(0.dp), modifier = Modifier.padding(top = 14.dp)) { Text("앱 정보와 기능 안내") }
                    Text("모먼트 ${BuildConfig.VERSION_NAME}", fontSize = 13.sp, color = Muted)
                }
                Sheet.ABOUT -> {
                    SheetTitle("앱 정보", "스타일 제작 방식과 촬영 기능을 확인하세요")
                    InfoText("스타일 제작 방식", "현재 스타일은 온도와 채도, 명암을 직접 조정해 만들었어요. 실제 아이폰 사진을 측정해 만든 값은 아니에요.")
                    InfoText("참고 자료의 범위", "애플 공식 문서는 카메라 사양을 확인하는 데 사용했어요. 촬영 리뷰에서는 조명에 따른 색과 화이트밸런스를 참고할 수 있지만, 이 자료가 앱의 보정 수치를 검증해 주는 것은 아니에요.")
                    val look = Looks.find(preferences.grade.lookId)
                    val evidence = look.evidence()
                    Text("현재 선택한 아이폰 ${look.model} 스타일의 참고 자료", color = Cream, fontSize = 13.sp, modifier = Modifier.padding(top = 16.dp))
                    SourceButton("애플 공식 카메라 사양", evidence.specification)
                    SourceButton("디엑스오마크 ${evidence.reviewModel} 촬영 리뷰", evidence.review)
                    if (look.id == "xs") Footnote("이 리뷰의 시험 기기는 XS Max예요. XS를 직접 시험한 자료와 구분해서 참고해야 해요.")
                    InfoText("사용할 수 있는 촬영 기능", "사진 촬영, 전후면 전환, 줌, 터치 초점, 초점 고정, 노출 조절, 플래시, 타이머를 제공해요. 실제 지원 범위는 현재 카메라에서 확인하며, 지원하지 않는 기능은 사용할 수 없어요.")
                    InfoText("휴대폰 기본 카메라와의 차이", "HDR과 야간 촬영, 인물 사진은 지원되는 기기에서 사용할 수 있어요. 배율을 높여도 반드시 망원 렌즈로 전환되는 것은 아니에요. 동영상, 연속 촬영, 파노라마, 수동 ISO와 셔터 속도, RAW 촬영은 제공하지 않아요.")
                    InfoText("사진 저장", "편집한 사진은 최대 약 1,600만 화소의 JPG로 저장해요. 큰 사진이나 메모리가 부족한 기기에서는 해상도가 낮아질 수 있어요. 가져온 사진의 비율은 그대로 유지돼요. 투명한 부분에는 흰 배경을 넣어 색감을 적용해요. 원본의 HDR 정보와 RAW 데이터는 JPG에 유지되지 않아요.")
                    InfoText("사진과 개인정보", "카메라 권한은 촬영에 사용해요. 가져오기는 직접 선택한 사진에만 접근해요. 앱의 색감 처리는 기기에서 이뤄지며 별도 서버나 분석 서비스로 사진을 전송하지 않아요. 갤러리 앱의 클라우드 동기화와 공유 기능은 해당 서비스 설정을 따라요.")
                    InfoText("사진에 포함된 정보", "편집본에는 촬영 기기와 노출 정보 등이 남을 수 있어요. 편집본에는 위치 정보를 복사하지 않지만, 별도 보관한 원본에는 기존 정보가 유지돼요. 사진은 갤러리에서 삭제할 수 있어요.")
                    Footnote("모먼트는 애플이나 삼성의 공식 앱이 아니에요. 모델 이름은 스타일의 영감을 설명하기 위해 사용해요.")
                }
            }
        }
    }
}

@Composable
private fun SheetTitle(title: String, subtitle: String) {
    Text(title, color = Cream, fontFamily = FontFamily.Serif, fontSize = 32.sp, letterSpacing = (-.8).sp)
    Text(subtitle, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 9.dp, bottom = 24.dp))
}

@Composable
private fun AdjustmentSlider(title: String, valueLabel: String, value: Float, range: ClosedFloatingPointRange<Float>, left: String, right: String, onChange: (Float) -> Unit) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Cream, fontSize = 14.sp)
            Text(valueLabel, color = Lime, fontSize = 13.sp)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range,
            colors = SliderDefaults.colors(activeTrackColor = Lime, inactiveTrackColor = Line, thumbColor = Lime),
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = title })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(left, color = Muted, fontSize = 10.sp); Text(right, color = Muted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SettingSwitch(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, color = Cream, fontSize = 14.sp)
            Text(subtitle, color = Muted, fontSize = 11.sp, lineHeight = 17.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Switch(checked, onChange, modifier = Modifier.semantics { contentDescription = title })
    }
}

@Composable
private fun Choice(title: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(onClick, modifier = modifier, shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, if (selected) Lime else Line),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Lime.copy(alpha = .1f) else Color.Transparent)) {
        Text(title, color = if (selected) Lime else Muted, fontSize = 12.sp)
    }
}

@Composable
private fun Footnote(text: String) { Text(text, color = Muted, fontSize = 11.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 12.dp)) }

private fun signed(value: Float) = String.format(Locale.US, "%+d", value.roundToInt())

@Composable
private fun InfoText(title: String, body: String) {
    Text(title, color = Cream, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 18.dp))
    Text(body, color = Muted, fontSize = 12.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 7.dp))
}

@Composable
private fun SourceButton(label: String, url: String) {
    val context = LocalContext.current
    var unavailable by remember { mutableStateOf(false) }
    TextButton(onClick = { unavailable = runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }.isFailure }, contentPadding = PaddingValues(0.dp)) {
        Text(label, fontSize = 12.sp)
    }
    if (unavailable) Text("링크를 열 수 있는 브라우저가 없어요.", color = Muted, fontSize = 11.sp)
}
