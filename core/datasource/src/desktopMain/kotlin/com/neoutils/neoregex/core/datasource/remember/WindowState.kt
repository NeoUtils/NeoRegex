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

package com.neoutils.neoregex.core.datasource.remember

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowState
import com.neoutils.neoregex.core.datasource.model.WindowStateData

@Composable
fun rememberWindowState(windowStateData: WindowStateData): WindowState {

    val density = LocalDensity.current

    return density.run {
        rememberWindowState(
            position = windowStateData.position?.let {
                WindowPosition.Absolute(
                    x = it.x.toDp(),
                    y = it.y.toDp()
                )
            } ?: WindowPosition.Aligned(
                alignment = Alignment.Center
            ),
            size = windowStateData.size.let {
                DpSize(
                    width = it.width.toDp(),
                    height = it.height.toDp()
                )
            },
            placement = when(windowStateData.placement) {
                WindowStateData.Placement.FLOATING -> WindowPlacement.Floating
                WindowStateData.Placement.MAXIMIZED -> WindowPlacement.Maximized
                WindowStateData.Placement.FULLSCREEN -> WindowPlacement.Fullscreen
            }
        )
    }
}
