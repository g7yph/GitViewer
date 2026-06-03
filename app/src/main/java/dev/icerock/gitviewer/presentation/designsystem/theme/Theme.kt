package dev.icerock.gitviewer.presentation.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue50,
    onPrimary = Color.Black,
    primaryContainer = Gray90,
    onPrimaryContainer = Color.Black,
    background = Gray90,
    onBackground = Gray40,
    surface = Gray80,
    onSurface = Gray70,
    error = Red40
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue30,
    onPrimary = Color.White,
    primaryContainer = Blue10,
    onPrimaryContainer = Color.White,
    background = Blue10,
    onBackground = Color.White,
    error = Red50,
    surface = Blue20,
    onSurface = Gray70
)

@Composable
internal fun GVTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme =  if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = GVTypography,
        content = content
    )
}