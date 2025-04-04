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

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectPressEvent(
    onPress: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
) {

    awaitEachGesture {

        awaitFirstDown()

        onPress?.invoke()

        val longPressTimeout = onLongPress?.let {
            viewConfiguration.longPressTimeoutMillis
        } ?: (Long.MAX_VALUE / 2)

        try {
            withTimeout(longPressTimeout) {
                waitForUpOrCancellation()
            }

            onRelease?.invoke()
        } catch (_: PointerEventTimeoutCancellationException) {
            onLongPress?.invoke()
        }

        waitForUpOrCancellation()

        onRelease?.invoke()
    }
}