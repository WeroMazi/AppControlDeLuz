package com.example.appcontroldeluz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    background = DarkAppThemeColors.background,
    surface = DarkAppThemeColors.surface,
    onBackground = DarkAppThemeColors.onBackground,
    onSurface = DarkAppThemeColors.onSurface,
    secondary = AccentBlue
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    background = LightAppThemeColors.background,
    surface = LightAppThemeColors.surface,
    onBackground = LightAppThemeColors.onBackground,
    onSurface = LightAppThemeColors.onSurface,
    secondary = AccentBlue
)

@Composable
fun AppControldeluzTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppThemeColors else LightAppThemeColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalAppThemeColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}