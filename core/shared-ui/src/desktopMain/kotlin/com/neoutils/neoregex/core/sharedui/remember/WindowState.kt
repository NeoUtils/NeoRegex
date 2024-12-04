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

package com.neoutils.neoregex.core.sharedui.remember

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.FrameWindowScope
import com.neoutils.neoregex.core.sharedui.extension.isFloating
import com.neoutils.neoregex.core.sharedui.extension.isFullMaximized
import com.neoutils.neoregex.core.sharedui.extension.isFullscreen
import java.awt.event.WindowStateListener


@Composable
fun FrameWindowScope.rememberNeoWindowState(): NeoWindowState {

    var state by remember { mutableStateOf(NeoWindowState.of(window)) }

    DisposableEffect(window) {

        val stateListener = WindowStateListener {
            state = NeoWindowState.of(window)
        }

        window.addWindowStateListener(stateListener)

        onDispose {
            window.removeWindowStateListener(stateListener)
        }
    }

    return state
}

enum class NeoWindowState {
    FLOATING,
    MAXIMIZED,
    FULLSCREEN,
    MINIMIZED;

    companion object {
        fun of(state: ComposeWindow): NeoWindowState {

            return when {
                state.isFullscreen -> FULLSCREEN
                state.isMinimized -> MINIMIZED
                state.isFullMaximized -> MAXIMIZED
                state.isFloating -> FLOATING
                else -> error("Unknown window state")
            }
        }
    }
}
