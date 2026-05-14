package com.example.grameenlight.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🎨 Colors
val BgDark = Color(0xFF0B1220)
val BgSoft = Color(0xFF0F1A2E)
val PrimaryGreen = Color(0xFF00FF9D)
val Blue = Color(0xFF3B82F6)
val Red = Color(0xFFEF4444)
val Yellow = Color(0xFFFACC15)
val CardGlass = Color.White.copy(alpha = 0.06f)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreen,
    background = BgDark,
    surface = BgSoft,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun GrameenLightTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF00FFD1),
        background = Color(0xFF0F0F0F),
        surface = Color(0xFF1A1A1A),
        secondary = Color(0xFFB0B0B0)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
