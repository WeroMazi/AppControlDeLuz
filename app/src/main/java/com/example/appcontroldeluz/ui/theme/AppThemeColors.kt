package com.example.appcontroldeluz.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onSurfaceVariant: Color,
    val border: Color,
    val gradientStart: Color,
    val gradientEnd: Color,
    val subtleContainer: Color,
    val inputContainer: Color,
    val inputBorder: Color
)

val DarkAppThemeColors = AppThemeColors(
    background = Color(0xFF0F1420),
    surface = Color(0xFF1A2234),
    surfaceVariant = Color(0xFF242F46),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF9CA3AF),
    border = Color.White.copy(alpha = 0.08f),
    gradientStart = Color(0xFF1E2636),
    gradientEnd = Color(0xFF171E2B),
    subtleContainer = Color.White.copy(alpha = 0.05f),
    inputContainer = Color.White.copy(alpha = 0.05f),
    inputBorder = Color.White.copy(alpha = 0.1f)
)

val LightAppThemeColors = AppThemeColors(
    background = Color(0xFFF1F6FD),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE3ECF9),
    onBackground = Color(0xFF0F1420),
    onSurface = Color(0xFF0F1420),
    onSurfaceVariant = Color(0xFF506174),
    border = Color(0xFFD4E0EF),
    gradientStart = Color(0xFFF8FBFF),
    gradientEnd = Color(0xFFE3ECF9),
    subtleContainer = Color(0xFFEAF2FC),
    inputContainer = Color(0xFFFDFEFF),
    inputBorder = Color(0xFFD4E0EF)
)

val LocalAppThemeColors = staticCompositionLocalOf { DarkAppThemeColors }