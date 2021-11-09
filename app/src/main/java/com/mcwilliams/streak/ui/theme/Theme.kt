package com.mcwilliams.streak.ui.theme

import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat

private val Yellow400 = Color(0xFFF6E547)
private val Yellow600 = Color(0xFFF5CF1B)
private val Yellow700 = Color(0xFFF3B711)
private val Yellow800 = Color(0xFFF29F05)

private val Blue200 = Color(0xFF036e9a)
private val Blue400 = Color(0xFF4860F7)
private val Blue500 = Color(0xFF0540F2)
private val Blue800 = Color(0xFF001CCF)

private val Red300 = Color(0xFFEA6D7E)
private val Red800 = Color(0xFFD00036)

private val SkyBlue = Color(0xFF059EDC)

private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    secondary = SkyBlue,
    error = Red300,
)

val LightColorScheme = lightColorScheme(
    primary = Blue200,
    onPrimary = Color.Black,
    secondary = Yellow700,
    onSecondary = Color.Black,
    surface = SkyBlue,
    onSurface = Color.White,
    onBackground = Color.White,
    error = Red800,
    onError = Color.White
)

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun StreakTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {

    // Dynamic color is available on Android 12+
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme: ColorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
//        typography = Typography,
//        shapes = Shapes,
        content = content
    )
}

@Composable
fun Material3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
        }
    }

    androidx.compose.material3.MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}