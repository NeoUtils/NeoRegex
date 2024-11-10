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

package com.neoutils.neoregex.core.sharedui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.neoutils.neoregex.core.designsystem.theme.*

private val LightColors = lightColorScheme(
    primary = Gray800,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Gray200,
    onSurfaceVariant = Color.Black,
    surfaceTint = Color.White,
    surfaceContainer = Gray100,
    surfaceBright = Gray300,
    inverseSurface = Gray800,
    secondary = Blue100,
    secondaryContainer = Gray300,
    onSecondaryContainer = Color.Black,
    error = Red700,
)

@Composable
fun NeoErrorTheme(
    content: @Composable () -> Unit
) {
    NeoBaseTheme(
        colorScheme = LightColors,
        content = content
    )
}
