package dev.icerock.gitviewer.presentation.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue50,
    onPrimary = Color.White,
    primaryContainer = Blue10,
    onPrimaryContainer = Color.White,
    background = Blue10,
    onBackground = Color.White,
    error = Red50
)

@Composable
internal fun GVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = GVTypography,
        content = content
    )
}