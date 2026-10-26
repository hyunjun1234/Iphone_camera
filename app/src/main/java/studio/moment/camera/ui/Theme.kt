package studio.moment.camera.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF111410)
val Panel = Color(0xFF1E231C)
val Lime = Color(0xFFD9EE93)
val Cream = Color(0xFFF3F1E8)
val Muted = Color(0xFF9EA596)
val Line = Color(0xFF343B30)

@Composable
fun MomentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Lime, onPrimary = Ink, background = Ink, onBackground = Cream,
            surface = Panel, onSurface = Cream, surfaceVariant = Line,
            onSurfaceVariant = Muted, outline = Line,
        ), content = content,
    )
}
