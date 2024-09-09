package com.neo.regex.ui.screen.home

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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.neo.regex.core.domain.model.Target
import com.neo.regex.core.extension.onLongHold
import com.neo.regex.core.extension.toInput
import com.neo.regex.core.sharedui.TextEditor
import com.neo.regex.core.util.Command
import com.neo.regex.designsystem.textfield.NeoTextField
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import com.neo.regex.resources.Res
import com.neo.regex.resources.ic_redo_24
import com.neo.regex.resources.ic_undo_24
import com.neo.regex.resources.insert_regex_hint
import com.neo.regex.ui.screen.home.action.HomeAction
import com.neo.regex.ui.screen.home.state.HomeUiState
import com.neo.regex.ui.screen.home.state.error
import com.neo.regex.ui.screen.home.state.matches
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class HomeScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier.fillMaxSize()
    ) {

        val viewModel = rememberScreenModel { HomeViewModel() }

        val uiState by viewModel.uiState.collectAsState()

        TextEditor(
            value = uiState.text,
            onValueChange = {
                viewModel.onAction(
                    HomeAction.Input.UpdateText(it.toInput())
                )
            },
            onFocusChange = {
                if (it.isFocused) {
                    viewModel.onAction(
                        HomeAction.TargetChange(Target.TEXT)
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
                                HomeAction.History.Undo(Target.TEXT)
                            )
                            true
                        }

                        Command.REDO -> {
                            viewModel.onAction(
                                HomeAction.History.Redo(Target.TEXT)
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
        uiState: HomeUiState,
        onAction: (HomeAction) -> Unit,
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
                        HomeAction.Input.UpdateRegex(
                            it.toInput(allowMultiline = false)
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
                                HomeAction.TargetChange(Target.REGEX)
                            )
                        }
                    }
                    .onPreviewKeyEvent {
                        when (Command.from(it)) {
                            Command.UNDO -> {
                                onAction(
                                    HomeAction.History.Undo(Target.REGEX)
                                )
                                true
                            }

                            Command.REDO -> {
                                onAction(
                                    HomeAction.History.Redo(Target.REGEX)
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
        state: HomeUiState.History,
        onAction: (HomeAction.History) -> Unit,
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
                        HomeAction.History.Undo()
                    )
                }
                .onLongHold {
                    onAction(
                        HomeAction.History.Undo()
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
                        HomeAction.History.Redo()
                    )
                }
                .onLongHold {
                    onAction(
                        HomeAction.History.Redo()
                    )
                }
                .padding(
                    vertical = dimensions.tiny,
                    horizontal = dimensions.small,
                )
        )
    }
}