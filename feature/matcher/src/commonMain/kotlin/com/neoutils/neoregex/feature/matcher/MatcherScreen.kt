/*
 * NeoRegex.
 *
 * Copyright (C) 2024 <AUTHOR>.
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.neoutils.neoregex.core.common.util.Command
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.component.TextEditor
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.extension.onLongHold
import com.neoutils.neoregex.feature.matcher.extension.toTextState
import com.neoutils.neoregex.feature.matcher.model.Target
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import com.neoutils.neoregex.feature.matcher.state.error
import com.neoutils.neoregex.feature.matcher.state.matches
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.ic_redo_24
import com.neoutils.neoregex.core.resources.ic_undo_24
import com.neoutils.neoregex.core.resources.insert_regex_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class MatcherScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {

        val viewModel = rememberScreenModel { MatcherViewModel() }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        TextEditor(
            value = uiState.text,
            onValueChange = {
                viewModel.onAction(
                    MatcherAction.Input.UpdateText(it.toTextState())
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
            modifier = Modifier
                .weight(weight = 1f)
                .onPreviewKeyEvent {
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
            matches = uiState.matchResult.matches,
        )

        Footer(
            uiState = uiState,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    @Composable
    private fun Footer(
        uiState: MatcherUiState,
        onAction: (MatcherAction) -> Unit,
        modifier: Modifier = Modifier
    ) = Surface(
        modifier = modifier,
        shape = RectangleShape,
        shadowElevation = dimensions.small,
        color = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
    ) {
        Row(Modifier.fillMaxWidth()) {
            NeoTextField(
                value = uiState.regex,
                onValueChange = {
                    onAction(
                        MatcherAction.Input.UpdateRegex(
                            it.toTextState(allowMultiline = false)
                        )
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(weight = 1f)
                    .onFocusChanged {
                        if (it.isFocused) {
                            onAction(
                                MatcherAction.TargetChange(Target.REGEX)
                            )
                        }
                    }
                    .onPreviewKeyEvent {
                        when (Command.from(it)) {
                            Command.UNDO -> {
                                onAction(
                                    MatcherAction.History.Undo(Target.REGEX)
                                )
                                true
                            }

                            Command.REDO -> {
                                onAction(
                                    MatcherAction.History.Redo(Target.REGEX)
                                )
                                true
                            }

                            else -> false
                        }
                    },
                hint = stringResource(Res.string.insert_regex_hint),
                error = uiState.matchResult.error
            )

            HistoryControl(
                state = uiState.history,
                onAction = onAction,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(vertical = dimensions.small)
                    .padding(end = dimensions.small)
                    .focusProperties {
                        canFocus = false
                    }
            )
        }
    }

    @Composable
    private fun HistoryControl(
        state: MatcherUiState.History,
        onAction: (MatcherAction.History) -> Unit,
        modifier: Modifier = Modifier,
        shape: CornerBasedShape = RoundedCornerShape(dimensions.small)
    ) = Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .border(
                width = 1.dp,
                color = colorScheme.outline,
                shape = RoundedCornerShape(dimensions.small)
            )
    ) {

        val contentColor = LocalContentColor.current

        Icon(
            painter = painterResource(Res.drawable.ic_undo_24),
            contentDescription = null,
            tint = contentColor.copy(
                alpha = if (state.canUndo) 1f else 0.5f
            ),
            modifier = Modifier
                .clip(
                    shape.copy(
                        topEnd = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    )
                )
                .clickable(state.canUndo) {
                    onAction(
                        MatcherAction.History.Undo()
                    )
                }
                .onLongHold {
                    onAction(
                        MatcherAction.History.Undo()
                    )
                }
                .padding(
                    vertical = dimensions.tiny,
                    horizontal = dimensions.small,
                )
        )

        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .weight(
                    weight = 1f,
                    fill = false
                )
        )

        Icon(
            painter = painterResource(Res.drawable.ic_redo_24),
            contentDescription = null,
            tint = contentColor.copy(
                alpha = if (state.canRedo) 1f else 0.5f
            ),
            modifier = Modifier
                .clip(
                    shape.copy(
                        topStart = CornerSize(0.dp),
                        bottomStart = CornerSize(0.dp)
                    )
                )
                .clickable(state.canRedo) {
                    onAction(
                        MatcherAction.History.Redo()
                    )
                }
                .onLongHold {
                    onAction(
                        MatcherAction.History.Redo()
                    )
                }
                .padding(
                    vertical = dimensions.tiny,
                    horizontal = dimensions.small,
                )
        )
    }
}
