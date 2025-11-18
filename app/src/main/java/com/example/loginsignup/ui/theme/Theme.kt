package com.example.loginsignup.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme

import androidx.compose.runtime.Composable


import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

val DarkModernBackground = Color(0xFF0F1115)   // main background
val DarkModernSurface = Color(0xFF1A1D23)      // cards, text fields
val DarkModernBorder = Color(0xFF2B2F36)       // outlines / dividers
val DarkModernText = Color(0xFFFFFFFF)         // primary text
val DarkModernHint = Color(0xFF9CA3AF)         // secondary / hint text
val DarkModernAccent = Color(0xFF00E0C7)       // teal accent (buttons)
val DarkModernError = Color(0xFFFF4D4F)        // error red

private val AppTypography = Typography()
private val DarkModernColorScheme = darkColorScheme(
    primary = DarkModernAccent,
    onPrimary = Color.Black,
    secondary = DarkModernAccent,
    onSecondary = Color.Black,
    background = DarkModernBackground,
    surface = DarkModernSurface,
    onBackground = DarkModernText,
    onSurface = DarkModernText,
    error = DarkModernError,
    onError = Color.White
)

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkModernColorScheme,
        typography = AppTypography,
        content = content
    )
}