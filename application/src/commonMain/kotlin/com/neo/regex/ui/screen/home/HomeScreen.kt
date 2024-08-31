package com.neo.regex.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.neo.regex.core.domain.Target
import com.neo.regex.core.extension.onLongHold
import com.neo.regex.core.extension.redo
import com.neo.regex.core.extension.undo
import com.neo.regex.core.sharedui.TextEditor
import com.neo.regex.designsystem.textfield.NeoTextField
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import com.neo.regex.resources.Res
import com.neo.regex.resources.ic_redo_24
import com.neo.regex.resources.ic_undo_24
import com.neo.regex.resources.insert_regex_hint
import com.neo.regex.ui.screen.home.action.HomeAction
import com.neo.regex.ui.screen.home.state.HomeUiState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) = Column(
    modifier = Modifier
        .background(colorScheme.background)
        .fillMaxSize()
) {

    // TODO(improve): improve when upgrading to jetpack compose 1.7.0
    val uiState by viewModel.uiState.collectAsState()

    TextEditor(
        value = uiState.text,
        onValueChange = {
            viewModel.onAction(
                HomeAction.UpdateText(it)
            )
        },
        modifier = Modifier
            .weight(weight = 1f)
            .onPreviewKeyEvent {
                when {
                    it.undo -> {
                        viewModel.onAction(
                            HomeAction.History.Undo(Target.TEXT)
                        )
                        true
                    }

                    it.redo -> {
                        viewModel.onAction(
                            HomeAction.History.Redo(Target.TEXT)
                        )
                        true
                    }

                    else -> false
                }
            },
        onFocusChange = {
            if (it.isFocused) {
                viewModel.onAction(
                    HomeAction.TargetChange(Target.TEXT)
                )
            }
        }
    )

    Footer(
        modifier = Modifier.fillMaxWidth(),
        onAction = viewModel::onAction,
        uiState = uiState,
    )
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier,
    onAction: (HomeAction) -> Unit,
    uiState: HomeUiState
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        NeoTextField(
            value = uiState.regex,
            onValueChange = {
                onAction(
                    HomeAction.UpdateRegex(it)
                )
            },
            hint = {
                Text(
                    text = stringResource(Res.string.insert_regex_hint),
                    style = typography.bodyLarge
                )
            },
            singleLine = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(dimensions.default)
                .weight(weight = 1f)
                .onFocusChanged {
                    if (it.isFocused) {
                        onAction(
                            HomeAction.TargetChange(Target.REGEX)
                        )
                    }
                }
                .onPreviewKeyEvent {
                    when {
                        it.undo -> {
                            onAction(
                                HomeAction.History.Undo(Target.REGEX)
                            )
                            true
                        }

                        it.redo -> {
                            onAction(
                                HomeAction.History.Redo(Target.REGEX)
                            )
                            true
                        }

                        else -> false
                    }
                }
        )

        HistoryControl(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(dimensions.small)
                .focusProperties {
                    canFocus = false
                },
            onAction = onAction,
            state = uiState.history
        )
    }
}

@Composable
private fun HistoryControl(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(dimensions.small),
    onAction: (HomeAction.History) -> Unit,
    state: HomeUiState.History
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min)
        .border(
            width = 1.dp,
            color = colorScheme.outline,
            shape = RoundedCornerShape(dimensions.small)
        )
) {


    Icon(
        painter = painterResource(Res.drawable.ic_undo_24),
        contentDescription = null,
        modifier = Modifier
            .clip(
                shape.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            )
            .clickable(
                enabled = state.canUndo
            ) {
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
            ),
        tint = colorScheme.onSurfaceVariant.copy(
            alpha = if (state.canUndo) 1f else 0.5f
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
        modifier = Modifier
            .clip(
                shape.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
            )
            .clickable(
                enabled = state.canRedo
            ) {
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
            ),
        tint = colorScheme.onSurfaceVariant.copy(
            alpha = if (state.canRedo) 1f else 0.5f
        )
    )
}
