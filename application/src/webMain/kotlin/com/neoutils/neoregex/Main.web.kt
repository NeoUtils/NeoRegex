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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.CanvasBasedWindow
import com.neoutils.neoregex.core.common.extension.toCss
import com.neoutils.neoregex.core.common.util.SizeManager
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import kotlinx.browser.document
import kotlinx.coroutines.flow.first
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

                Experimental {
                    App()
                }
            }
        }
    }
}

@Composable
fun Experimental(
    content: @Composable () -> Unit
) = Box {
    content()

    Box(
        modifier = Modifier
            .size(200.dp)
            .align(Alignment.TopEnd)
    ) {
        Text(
            text = "experimental",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .graphicsLayer(
                    rotationZ = 45f,
                    translationX = 50f,
                    translationY = -50f
                )
                .background(Color.Yellow)
                .padding(
                    vertical = 8.dp
                ),
        )
    }
}
