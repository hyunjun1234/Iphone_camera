package studio.moment.camera.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.moment.camera.camera.CameraStatus
import studio.moment.camera.data.Preferences
import java.util.Locale
import kotlin.math.roundToInt

private enum class Adjustment(val label: String) {
    INTENSITY("강도"), WARMTH("온도"), CONTRAST("대비"), EXPOSURE("노출")
}

@Composable
internal fun TunePanel(
    preferences: Preferences,
    camera: CameraStatus,
    importing: Boolean,
    onUpdate: ((Preferences) -> Preferences) -> Unit,
    onExposure: (Float) -> Unit,
) {
    var selection by rememberSaveable { mutableStateOf(Adjustment.INTENSITY) }
    val exposureAvailable = !importing && camera.ready && camera.maxEv > camera.minEv
    val tabs = Adjustment.entries.filter { it != Adjustment.EXPOSURE || exposureAvailable }
    val active = selection.takeIf { it in tabs } ?: Adjustment.INTENSITY
    val grade = preferences.grade
    val resetEnabled = when (active) {
        Adjustment.INTENSITY -> grade.intensity != 1f
        Adjustment.WARMTH -> grade.warmth != 0f
        Adjustment.CONTRAST -> grade.tone != 0f
        Adjustment.EXPOSURE -> camera.ev != 0f
    }
    Surface(color = Panel, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth().testTag("tune-panel")) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { tab ->
                    Choice(tab.label, active == tab, Modifier.widthIn(min = 64.dp)) { selection = tab }
                }
            }
            when (active) {
                Adjustment.INTENSITY -> AdjustmentSlider("강도", "${(grade.intensity * 100).roundToInt()}%", grade.intensity, 0f..1f, "원본", "스타일") { value ->
                    onUpdate { it.copy(grade = it.grade.copy(intensity = value)) }
                }
                Adjustment.WARMTH -> AdjustmentSlider("온도", signed(grade.warmth * 100), grade.warmth, -1f..1f, "차갑게", "따뜻하게") { value ->
                    onUpdate { it.copy(grade = it.grade.copy(warmth = value)) }
                }
                Adjustment.CONTRAST -> AdjustmentSlider("대비", signed(grade.tone * 100), grade.tone, -1f..1f, "낮게", "높게") { value ->
                    onUpdate { it.copy(grade = it.grade.copy(tone = value)) }
                }
                Adjustment.EXPOSURE -> AdjustmentSlider("노출", String.format(Locale.US, "%+.1f EV", camera.ev), camera.ev.coerceIn(camera.minEv, camera.maxEv), camera.minEv..camera.maxEv, "어둡게", "밝게", onExposure)
            }
            TextButton(enabled = resetEnabled, onClick = {
                when (active) {
                    Adjustment.INTENSITY -> onUpdate { it.copy(grade = it.grade.copy(intensity = 1f)) }
                    Adjustment.WARMTH -> onUpdate { it.copy(grade = it.grade.copy(warmth = 0f)) }
                    Adjustment.CONTRAST -> onUpdate { it.copy(grade = it.grade.copy(tone = 0f)) }
                    Adjustment.EXPOSURE -> onExposure(0f)
                }
            }, contentPadding = PaddingValues(horizontal = 4.dp)) {
                Icon(Icons.Outlined.RestartAlt, null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("${active.label} 초기화", fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun AdjustmentSlider(title: String, valueLabel: String, value: Float, range: ClosedFloatingPointRange<Float>, left: String, right: String, onChange: (Float) -> Unit) {
    Column(Modifier.padding(top = 12.dp, bottom = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Cream, fontSize = 14.sp)
            Text(valueLabel, color = Lime, fontSize = 13.sp)
        }
        Slider(value = value, onValueChange = onChange, valueRange = range,
            colors = SliderDefaults.colors(activeTrackColor = Lime, inactiveTrackColor = Line, thumbColor = Lime),
            modifier = Modifier.fillMaxWidth().testTag("adjustment-slider").semantics { contentDescription = title; stateDescription = valueLabel })
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(left, color = Muted, fontSize = 11.sp)
            Text(right, color = Muted, fontSize = 11.sp)
        }
    }
}

private fun signed(value: Float) = String.format(Locale.US, "%+d", value.roundToInt())
