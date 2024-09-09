package com.neo.regex.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Blue700,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    surfaceTint = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Gray200,
    onSurfaceVariant = Color.Black,
    surfaceContainer = Gray100,
    secondary = Blue100,
    secondaryContainer = Gray300,
    onSecondaryContainer = Color.Black,
    error = Red700,
)

private val DarkColors = darkColorScheme(
    primary = Blue300,
    background = Gray900,
    onBackground = Color.White,
    surface = Gray900,
    surfaceTint = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Gray700,
    onSurfaceVariant = Color.White,
    surfaceContainer = Gray800,
    secondary = Blue700,
    secondaryContainer = Gray500,
    onSecondaryContainer = Color.White,
    error = Red600,
)

@Composable
fun NeoTheme(
    darkMode: Boolean = isSystemInDarkTheme(),
    fontSizes: FontSizes = FontSizes(),
    dimensions: Dimensions = Dimensions(),
    typography: Typography = NeoTypography,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkMode) {
            DarkColors
        } else {
            LightColors
        },
        typography = typography,
    ) {
        CompositionLocalProvider(
            LocalFontSizes provides fontSizes,
            LocalDimensions provides dimensions,
            content = content
        )
    }
}

@Composable
fun NeoBackground(
    modifier: Modifier = Modifier,
    color: Color = colorScheme.background,
    contentColor: Color = colorScheme.onBackground,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
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