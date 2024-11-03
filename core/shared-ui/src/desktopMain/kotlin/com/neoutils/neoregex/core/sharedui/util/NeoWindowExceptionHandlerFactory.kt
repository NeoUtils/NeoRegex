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

package com.neoutils.neoregex.core.sharedui.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.*
import com.neoutils.neoregex.core.sharedui.component.FatalErrorWindow
import com.neoutils.neoregex.core.sharedui.theme.NeoErrorTheme
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.skiko.MainUIDispatcher
import java.awt.event.WindowEvent
import javax.swing.SwingUtilities
import java.awt.Window as AwtWindows

private val mainScope = CoroutineScope(MainUIDispatcher)

@OptIn(ExperimentalComposeUiApi::class)
object NeoWindowExceptionHandlerFactory : WindowExceptionHandlerFactory {

    override fun exceptionHandler(window: AwtWindows) = WindowExceptionHandler { throwable ->

        SwingUtilities.invokeLater {
            mainScope.launchApplication {
                NeoErrorTheme {
                    FatalErrorWindow(throwable)
                }
            }

            window.dispatchEvent(
                WindowEvent(
                    window, WindowEvent.WINDOW_CLOSING
                )
            )
        }

        throw throwable
    }
}
