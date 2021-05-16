package com.mcwilliams.streak.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Yellow400 = Color(0xFFF6E547)
private val Yellow600 = Color(0xFFF5CF1B)
private val Yellow700 = Color(0xFFF3B711)
private val Yellow800 = Color(0xFFF29F05)

private val Blue200 = Color(0xFF059EDC)
private val Blue400 = Color(0xFF4860F7)
private val Blue500 = Color(0xFF0540F2)
private val Blue800 = Color(0xFF001CCF)

private val Red300 = Color(0xFFEA6D7E)
private val Red800 = Color(0xFFD00036)

private val SkyBlue = Color(0xFF059EDC)

private val StreakDarkPalette = darkColors(
    primary = Blue200,
    primaryVariant = Blue400,
    secondary = SkyBlue,
    error = Red300,
)

private val StreakLightPalette = lightColors(
    primary = Blue500,
    primaryVariant = Blue800,
    onPrimary = Color.White,
    secondary = Yellow700,
    secondaryVariant = Yellow800,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
    error = Red800,
    onError = Color.White
)

@Composable
fun StreakTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        StreakDarkPalette
    } else {
        StreakLightPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}