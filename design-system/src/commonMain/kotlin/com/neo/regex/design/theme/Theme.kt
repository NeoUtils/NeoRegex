package com.neo.regex.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    // TODO: Define colors
)

@Composable
fun NeoTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    fontSizes: FontSizes = FontSizes(),
    dimensions: Dimensions = Dimensions(),
    typography: Typography = NeoTypography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalFontSizes provides fontSizes,
        LocalDimensions provides dimensions
    ) {
        MaterialTheme(
            colorScheme = if (darkMode) {
                TODO("DarkColors is not defined")
            } else {
                LightColors
            },
            typography = typography,
            content = content
        )
    }
}

@Composable
fun NeoBackground(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.background,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        content = content
    )
}

object NeoTheme {

    val dimensions: Dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val fontSizes: FontSizes
        @Composable
        @ReadOnlyComposable
        get() = LocalFontSizes.current
}