package com.example.appcappucare.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColoresCappucare = lightColorScheme(
    primary = Color(0xFF00A89D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F5F0),
    onPrimaryContainer = Color(0xFF12304A),

    secondary = Color(0xFF12304A),
    onSecondary = Color.White,

    tertiary = Color(0xFF3478C8),
    onTertiary = Color.White,

    background = Color(0xFFF3F7FA),
    onBackground = Color(0xFF12304A),

    surface = Color.White,
    onSurface = Color(0xFF12304A),

    surfaceVariant = Color(0xFFEAF1F5),
    onSurfaceVariant = Color(0xFF657789),

    outline = Color(0xFFB6C8D2),

    error = Color(0xFFC63C43),
    onError = Color.White
)

@Composable
fun AppCappucareTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColoresCappucare,
        typography = Typography,
        content = content
    )
}
