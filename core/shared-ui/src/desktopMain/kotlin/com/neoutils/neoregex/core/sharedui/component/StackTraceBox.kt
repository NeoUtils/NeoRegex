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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.util.Padding

@Composable
fun StackTraceBox(
    throwable: Throwable,
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier,
) {

    val stackTrace = remember(throwable) {
        throwable.stackTraceToString().trim()
    }

    val density = LocalDensity.current

    val verticalScrollState = rememberScrollState()

    val horizontalScrollState = rememberScrollState()

    var padding by remember { mutableStateOf(Padding()) }

    SelectionContainer {
        Text(
            text = stackTrace,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(padding.values)
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
                .padding(dimensions.small.s)
        )
    }

    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(verticalScrollState),
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(bottom = padding.bottom)
            .onSizeChanged {
                padding = padding.copy(
                    end = with(density) { it.width.toDp() },
                )
            }
    )

    HorizontalScrollbar(
        adapter = rememberScrollbarAdapter(horizontalScrollState),
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(end = padding.end)
            .onSizeChanged {
                padding = padding.copy(
                    bottom = with(density) { it.height.toDp() },
                )
            }
    )
}
