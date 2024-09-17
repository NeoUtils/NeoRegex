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

package com.neo.regex.core.sharedui.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LineNumbers(
    count: Int,
    offset: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {

    val mergedTextStyle = typography.bodyMedium.copy(
        textAlign = TextAlign.End,
    ).merge(textStyle)

    val lines = remember(count) {
        // TODO(improve): isn't efficient, O(n)
        IntRange(1, count).joinToString(separator = "\n")
    }

    val scrollState = remember(offset) { ScrollState(offset) }

    // TODO(improve): it's not performant for a large number of lines
    BasicText(
        text = lines,
        style = mergedTextStyle,
        modifier = modifier.verticalScroll(
            state = scrollState,
            enabled = false
        ).padding(
            horizontal = 8.dp
        )
    )
}