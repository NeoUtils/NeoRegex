/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.designsystem.theme

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
    typography: Typography = NeoTypography(),
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
