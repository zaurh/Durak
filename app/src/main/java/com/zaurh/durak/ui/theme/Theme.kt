package com.zaurh.durak.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ThemeColors.Night.title,
    secondary = ThemeColors.Night.text,
    background = ThemeColors.Night.background,
    onBackground = ThemeColors.Night.messageBackground,
    surface = ThemeColors.Night.surface,
    tertiary = ThemeColors.Night.secondBackground,
    onTertiary = ThemeColors.Night.red,
    onSecondary = ThemeColors.Night.yellow,
    onSurfaceVariant = ThemeColors.Night.authColor
)

private val LightColorScheme = lightColorScheme(
    primary = ThemeColors.Day.title,
    secondary = ThemeColors.Day.text,
    background = ThemeColors.Day.background,
    onBackground = ThemeColors.Day.messageBackground,
    surface = ThemeColors.Day.surface,
    tertiary = ThemeColors.Day.secondBackground,
    onTertiary = ThemeColors.Day.red,
    onSecondary = ThemeColors.Day.yellow,
    onSurfaceVariant = ThemeColors.Day.authColor

)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */

@Composable
fun DurakTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}