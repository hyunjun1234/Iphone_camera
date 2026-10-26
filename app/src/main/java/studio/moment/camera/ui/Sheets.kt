package studio.moment.camera.ui

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
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Panel, contentColor = Cream,
        scrimColor = if (sheet == Sheet.TUNE) Color.Transparent else Color.Black.copy(alpha = .45f),
        dragHandle = { BottomSheetDefaults.DragHandle(color = Line) }) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 30.dp)) {
            when (sheet) {
                Sheet.LOOKS -> {
                    SheetTitle("Find your tone.", "같은 순간, 여덟 가지 분위기")
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
                                    Text("iPhone ${look.model}", color = Cream, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    Text("  ${look.year}", color = Muted, fontSize = 10.sp)
                                }
                                Text(look.title, color = Color(look.swatch), fontSize = 12.sp, modifier = Modifier.padding(top = 3.dp))
                                Text(look.description, color = Muted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                            if (selected) Icon(Icons.Outlined.CheckCircle, "선택됨", tint = Lime, modifier = Modifier.size(19.dp))
                        }
                    }
                    Footnote("각 모델에서 영감을 받은 색감이에요. 기기와 빛에 따라 결과는 달라질 수 있어요.")
                }
                Sheet.TUNE -> {
                    var adjustment by remember { mutableIntStateOf(0) }
                    SheetTitle("Make it yours.", "iPhone ${Looks.find(preferences.grade.lookId).model} · ${Looks.find(preferences.grade.lookId).title}")
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
                        Button(onClick = onDismiss, shape = RoundedCornerShape(50)) { Text("이 색감으로", modifier = Modifier.padding(horizontal = 12.dp)) }
                    }
                }
                Sheet.MODES -> {
                    SheetTitle("Capture the light.", "이 카메라에서 사용할 수 있는 촬영 모드")
                    CaptureMode.entries.forEach { mode ->
                        val available = mode in camera.availableModes
                        val selected = mode == camera.activeMode
                        val description = when (mode) {
                            CaptureMode.STANDARD -> "기본 촬영과 실시간 색감"
                            CaptureMode.AUTO -> "장면에 맞춰 제조사 처리를 자동 선택"
                            CaptureMode.HDR -> "밝은 곳과 어두운 곳의 디테일을 함께"
                            CaptureMode.NIGHT -> "어두운 순간도 밝게 · 잠시 고정해 주세요"
                            CaptureMode.PORTRAIT -> "인물은 또렷하게, 배경은 부드럽게"
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
                    Footnote("지원 모드는 휴대폰과 전·후면 카메라에 따라 달라요. 삼성 기본 카메라의 전용 기능이 필요하면, 촬영한 사진을 가져와 색감을 적용해 보세요.")
                }
                Sheet.SETTINGS -> {
                    SheetTitle("A little more you.", "나의 카메라 설정")
                    SettingSwitch("격자", "화면을 9등분해 구도를 맞춰요", preferences.grid) { value -> onUpdate { it.copy(grid = value) } }
                    SettingSwitch("원본도 저장", "촬영 원본을 moment / Originals에 보관해요", preferences.saveOriginal) { value -> onUpdate { it.copy(saveOriginal = value) } }
                    SettingSwitch("셀피 좌우 반전", "미리보기에서 보이는 방향으로 저장해요", preferences.mirrorSelfie) { value -> onUpdate { it.copy(mirrorSelfie = value) } }
                    SettingSwitch("셔터 진동", "촬영 버튼을 누르면 가볍게 알려줘요", preferences.haptics) { value -> onUpdate { it.copy(haptics = value) } }
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
                        Text("  사진은 내 휴대폰 안에", color = Cream, fontSize = 13.sp)
                    }
                    Footnote("모든 색감 처리는 기기에서 이뤄져요. 사진을 서버에 보내지 않아요. 선택한 모델과 설정은 다음에도 기억해요.")
                    Text("moment  0.1", fontFamily = FontFamily.Serif, fontSize = 23.sp, color = Muted, modifier = Modifier.padding(top = 17.dp))
                    Footnote("iPhone에서 영감을 받은 독립적인 카메라 앱입니다. Apple 및 Samsung의 공식 앱이 아니며, 기종의 카메라 처리 전체를 재현하지 않습니다.")
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
