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

package com.neoutils.neoregex.core.datasource.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.WindowPlacement
import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.model.WindowStateData
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.WindowStateListener

@Composable
fun WindowStateDataSource.observe(window: ComposeWindow) {

    DisposableEffect(window) {
        val componentListener = object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent) {
                if (window.placement == WindowPlacement.Floating) {
                    update {
                        it.copy(
                            position = WindowStateData.Position(
                                x = window.x,
                                y = window.y
                            )
                        )
                    }
                }
            }

            override fun componentResized(e: ComponentEvent) {
                if (window.placement == WindowPlacement.Floating) {
                    update {
                        it.copy(
                            size = WindowStateData.Size(
                                width = window.width,
                                height = window.height
                            )
                        )
                    }
                }
            }
        }

        val windowListener = WindowStateListener {
            update {
                it.copy(
                    placement = when (window.placement) {
                        WindowPlacement.Floating -> WindowStateData.Placement.FLOATING
                        WindowPlacement.Maximized -> WindowStateData.Placement.MAXIMIZED
                        WindowPlacement.Fullscreen -> WindowStateData.Placement.FULLSCREEN
                    }
                )
            }
        }

        window.addComponentListener(componentListener)
        window.addWindowStateListener(windowListener)

        onDispose {
            window.removeComponentListener(componentListener)
            window.removeWindowStateListener(windowListener)
        }
    }
}