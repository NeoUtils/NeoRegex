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

import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.roboto_mono
import org.jetbrains.compose.resources.Font

private val LightColors = lightColorScheme(
    primary = Blue700,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Gray200,
    onSurfaceVariant = Color.Black,
    surfaceTint = Color.White,
    surfaceContainer = Gray100,
    surfaceBright = Gray300,
    secondary = Blue100,
    secondaryContainer = Gray300,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = Green,
    error = Red700,
)

private val DarkColors = darkColorScheme(
    primary = Blue300,
    background = Gray900,
    onBackground = Color.White,
    surface = Gray900,
    onSurface = Color.White,
    surfaceVariant = Gray700,
    onSurfaceVariant = Color.White,
    surfaceTint = Color.Black,
    surfaceContainer = Gray800,
    surfaceBright = Gray500,
    secondary = Blue700,
    secondaryContainer = Gray500,
    onSecondaryContainer = Color.White,
    tertiaryContainer = Green,
    error = Red600,
)

@Composable
fun NeoTheme(
    colorTheme: ColorTheme = rememberColorTheme(),
    typography: Typography = NeoTypography(
        fontFamily = FontFamily(Font(Res.font.roboto_mono))
    ),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColorTheme provides colorTheme
    ) {
        NeoBaseTheme(
            colorScheme = when (colorTheme) {
                ColorTheme.LIGHT, ColorTheme.LIGHT_SYSTEM -> LightColors
                ColorTheme.DARK, ColorTheme.DARK_SYSTEM -> DarkColors
            },
            fontSizes = FontSizes(),
            dimensions = Dimensions(),
            typography = typography,
            content = content
        )
    }
}

@Composable
fun NeoBaseTheme(
    colorScheme: ColorScheme,
    fontSizes: FontSizes = FontSizes(),
    dimensions: Dimensions = Dimensions(),
    typography: Typography = NeoTypography(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
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
