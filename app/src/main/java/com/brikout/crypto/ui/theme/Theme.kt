package com.brikout.crypto.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BrikDarkScheme = darkColorScheme(
    primary = AccentBlue,
    background = DarkBackground,
    surface = DarkSurface
)

@Composable
fun BrikTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrikDarkScheme,
        typography = Typography,
        content = content
    )
}
