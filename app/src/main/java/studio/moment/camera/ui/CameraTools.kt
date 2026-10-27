package studio.moment.camera.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

/** A stable row outside the saved frame, including while the zoom slider is open. */
@Composable
internal fun CameraTools(
    zoom: Float, minZoom: Float, maxZoom: Float, camera: Boolean, enabled: Boolean,
    expanded: Boolean, interaction: Int, onExpand: () -> Unit, onCollapse: () -> Unit,
    onZoom: (Float) -> Unit, comparing: Boolean, onCompare: () -> Unit, onComparePress: (Boolean) -> Unit,
) {
    val canZoom = camera && maxZoom > minZoom
    val showSlider = canZoom && expanded
    val sliderInteractions = remember { MutableInteractionSource() }
    val dragging by sliderInteractions.collectIsDraggedAsState()
    val pressed by sliderInteractions.collectIsPressedAsState()
    val collapse by rememberUpdatedState(onCollapse)
    val timeout = LocalAccessibilityManager.current?.calculateRecommendedTimeoutMillis(
        originalTimeoutMillis = 3_500, containsIcons = true, containsText = true, containsControls = true,
    ) ?: 3_500L
    LaunchedEffect(showSlider, interaction, dragging, pressed, timeout) {
        if (showSlider && !dragging && !pressed) { delay(timeout); collapse() }
    }
    Row(Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 12.dp).testTag("camera-tools"),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        if (showSlider) {
            TextButton(onClick = { onZoom(1f.coerceIn(minZoom, maxZoom)) }, enabled = enabled,
                modifier = Modifier.height(48.dp).testTag("zoom-reset").semantics {
                    contentDescription = "기본 배율로 돌아가기"
                    stateDescription = "${formatZoom(zoom)}배"
                }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("${formatZoom(zoom)}배", color = Lime, fontSize = 12.sp, maxLines = 1)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Outlined.RestartAlt, null, Modifier.size(16.dp), tint = Lime)
            }
            Slider(value = zoom.coerceIn(minZoom, maxZoom), onValueChange = onZoom,
                valueRange = minZoom..maxZoom, enabled = enabled, interactionSource = sliderInteractions,
                modifier = Modifier.weight(1f).testTag("zoom-slider").semantics {
                    contentDescription = "배율 조절"
                    stateDescription = "${formatZoom(zoom)}배"
                }, colors = SliderDefaults.colors(thumbColor = Lime, activeTrackColor = Lime))
            ToolIcon(Icons.Outlined.Close, "배율 조절 닫기", onClick = onCollapse)
        } else {
            if (camera) TextButton(onClick = onExpand, enabled = enabled && canZoom,
                modifier = Modifier.height(48.dp).testTag("zoom-toggle").semantics {
                    contentDescription = "배율 조절 열기"
                    stateDescription = "${formatZoom(zoom)}배"
                }, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("${formatZoom(zoom)}배", color = if (enabled) Lime else Muted, fontSize = 12.sp, maxLines = 1)
                if (canZoom) Icon(Icons.Outlined.ExpandLess, null, Modifier.size(16.dp), tint = Lime)
            }
            Spacer(Modifier.weight(1f))
            CompareButton(comparing, enabled, onToggle = onCompare, onPress = onComparePress)
        }
    }
}

private fun formatZoom(zoom: Float): String = if (zoom % 1f == 0f) zoom.toInt().toString() else String.format(Locale.US, "%.1f", zoom)
