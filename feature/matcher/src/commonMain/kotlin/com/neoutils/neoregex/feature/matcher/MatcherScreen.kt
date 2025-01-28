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

package com.neoutils.neoregex.feature.matcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.neoutils.neoregex.core.common.extension.toTextState
import com.neoutils.neoregex.core.common.model.Target
import com.neoutils.neoregex.core.common.util.Command
import com.neoutils.neoregex.core.sharedui.component.Footer
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.core.sharedui.component.TextEditor
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState

class MatcherScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {

        val viewModel = rememberScreenModel { MatcherViewModel() }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        BoxWithConstraints(modifier = Modifier.weight(weight = 1f)) {

            TextEditor(
                value = uiState.inputs.text,
                onValueChange = {
                    viewModel.onAction(
                        MatcherAction.UpdateText(it.toTextState())
                    )
                },
                onFocusChange = {
                    if (it.isFocused) {
                        viewModel.onAction(
                            MatcherAction.TargetChange(Target.TEXT)
                        )
                    }
                },
                textStyle = TextStyle(
                    letterSpacing = 1.sp,
                    fontSize = 16.sp,
                ),
                matches = remember(uiState.result) {
                    when (val result = uiState.result) {
                        is MatcherUiState.Result.Failure -> listOf()
                        is MatcherUiState.Result.Success -> result.matches
                    }
                },
                modifier = Modifier.onPreviewKeyEvent {
                    when (Command.from(it)) {
                        Command.UNDO -> {
                            viewModel.onAction(
                                MatcherAction.History.Undo(Target.TEXT)
                            )
                            true
                        }

                        Command.REDO -> {
                            viewModel.onAction(
                                MatcherAction.History.Redo(Target.TEXT)
                            )
                            true
                        }

                        else -> false
                    }
                },
            )

            Performance(uiState.performance)
        }

        Footer(
            inputs = uiState.inputs,
            history = uiState.history,
            onAction = viewModel::onAction,
            error = remember(uiState.result) {
                when (val result = uiState.result) {
                    is MatcherUiState.Result.Failure -> result.error
                    is MatcherUiState.Result.Success -> ""
                }
            },
            onFocus = {
                if (it.isFocused) {
                    viewModel.onAction(
                        MatcherAction.TargetChange(Target.REGEX)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
