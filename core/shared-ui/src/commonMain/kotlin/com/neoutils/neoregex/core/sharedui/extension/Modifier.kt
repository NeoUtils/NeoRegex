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

package com.neoutils.neoregex.core.sharedui.extension

import androidx.compose.foundation.background
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Stable
fun Modifier.surface(
    shape: Shape,
    backgroundColor: Color,
    shadowElevation: Float,
): Modifier {

    return then(
        if (shadowElevation > 0f) {
            Modifier.graphicsLayer(
                shadowElevation = shadowElevation,
                shape = shape,
                clip = false
            )
        } else {
            Modifier
        }
    )
        .background(
            color = backgroundColor,
            shape = shape
        )
        .clip(shape)
}

fun Modifier.onLongHold(
    delayMillis: Long = 10L,
    action: () -> Unit
) = composed {

    val scope = rememberCoroutineScope()

    pointerInput(Unit) {

        var job: Job? = null

        detectPressEvent(
            onRelease = {
                job?.cancel()
            },
            onLongPress = {
                job = scope.launch {
                    while (true) {
                        action()
                        delay(delayMillis)
                    }
                }
            },
        )
    }
}