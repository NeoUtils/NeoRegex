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
import androidx.compose.ui.window.FrameWindowScope
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

enum class WindowFocus {
    FOCUSED,
    UNFOCUSED;
}

@Composable
fun FrameWindowScope.rememberWindowFocus(): WindowFocus {

    var focus by remember {
        mutableStateOf(
            if (window.isActive) {
                WindowFocus.FOCUSED
            } else {
                WindowFocus.UNFOCUSED
            }
        )
    }

    DisposableEffect(window) {

        val activationListener = object : WindowAdapter() {
            override fun windowActivated(e: WindowEvent) {
                focus = WindowFocus.FOCUSED
            }

            override fun windowDeactivated(e: WindowEvent) {
                focus = WindowFocus.UNFOCUSED
            }
        }

        window.addWindowListener(activationListener)

        onDispose {
            window.removeWindowListener(activationListener)
        }
    }

    return focus
}
