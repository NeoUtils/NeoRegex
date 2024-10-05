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

package com.neoutils.neoregex

import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.CanvasBasedWindow
import com.neoutils.neoregex.core.common.extension.toCss
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        val body = checkNotNull(document.body)

        val sizeManager = SizeManager().apply {
            resize()
        }

        CanvasBasedWindow(
            canvasElementId = "viewport-container",
            applyDefaultStyles = false,
            requestResize = {
                sizeManager.changes.first()
            }
        ) {
            NeoTheme {

                body.style.backgroundColor =
                    colorScheme.background.toCss()

                App()
            }
        }
    }
}

class SizeManager {

    private val _changes = Channel<IntSize>(CONFLATED)
    val changes get() = _changes.receiveAsFlow()

    init {
        window.asDynamic()
            .visualViewport
            .onresize = ::resize
    }

    fun resize() {
        _changes.trySend(
            IntSize(
                window.innerWidth,
                window.asDynamic().visualViewport.height as Int
            )
        )
    }
}
