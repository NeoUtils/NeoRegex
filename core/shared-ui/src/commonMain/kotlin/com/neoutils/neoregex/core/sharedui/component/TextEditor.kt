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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextStyle
import com.neoutils.neoregex.core.common.model.Text
import com.neoutils.neoregex.core.common.model.Match

@Composable
expect fun TextEditor(
    value: Text,
    onValueChange: (Text) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChange: (FocusState) -> Unit = {},
    matches: List<Match> = listOf(),
    textStyle: TextStyle = TextStyle(),
)