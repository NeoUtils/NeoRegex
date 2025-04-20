/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.common.util.Syntax
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.sharedui.event.FooterAction
import org.koin.compose.koinInject

@Composable
expect fun Footer(
    pattern: TextState,
    history: HistoryState,
    modifier: Modifier = Modifier,
    salvageManager: SalvageManager = koinInject(),
    onFocus: (FocusState) -> Unit = {},
    onAction: (FooterAction) -> Unit = {},
    tooling: (@Composable () -> Unit)? = null,
    syntax: Syntax.Regex = remember { Syntax.Regex() },
)