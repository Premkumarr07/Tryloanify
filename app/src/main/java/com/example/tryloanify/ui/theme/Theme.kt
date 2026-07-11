package com.example.tryloanify.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Appcolors.Primary,
    onPrimary = Color.White,
    secondary = Appcolors.PrimaryDark,
    background = Appcolors.Background,
    surface = Appcolors.Surface,
    onBackground = Appcolors.Text,
    onSurface = Appcolors.Text,
    error = Appcolors.Error,
)

@Composable
fun TryLoanifyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content,
    )
}
