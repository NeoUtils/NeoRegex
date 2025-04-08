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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.highlight.compose.remember.rememberTextFieldValue
import com.neoutils.neoregex.core.common.extension.toText
import com.neoutils.neoregex.core.common.extension.toTextFieldValue
import com.neoutils.neoregex.core.common.model.Field
import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.common.platform.isAndroid
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.common.util.Command
import com.neoutils.neoregex.core.common.util.Syntax
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.matcher_footer_insert_regex_hint
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

sealed class FooterAction {
    data class UpdateRegex(
        val text: TextState
    ) : FooterAction()

    sealed class History : FooterAction() {

        abstract val field: Field?

        data class Undo(
            override val field: Field? = null
        ) : History()

        data class Redo(
            override val field: Field? = null
        ) : History()
    }
}

@Composable
fun Footer(
    pattern: TextState,
    history: HistoryState,
    modifier: Modifier = Modifier,
    salvageManager: SalvageManager = koinInject(),
    onFocus: (FocusState) -> Unit = {},
    onAction: (FooterAction) -> Unit = {},
    tooling: (@Composable () -> Unit)? = null,
    syntax: Syntax.Regex = remember { Syntax.Regex() },
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small,
    color = colorScheme.surfaceContainer,
    contentColor = colorScheme.onSurface,
) {

    val coroutine = rememberCoroutineScope()

    Column {
        if (platform.isAndroid) {
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = salvageManager
                    .flow
                    .collectAsStateWithLifecycle(
                        initialValue = null
                    ).value,
                contentKey = { it != null },
                transitionSpec = {
                    val showUp = fadeIn() + slideIntoContainer(SlideDirection.Up)
                    val hideDown = fadeOut() + slideOutOfContainer(SlideDirection.Down)

                    showUp togetherWith hideDown
                },
                contentAlignment = Alignment.Center
            ) { opened ->
                if (opened != null) {
                    SalvageUi(
                        modifier = Modifier.height(30.dp),
                        opened = opened,
                        onAction = { action ->
                            when (action) {
                                SalvageAction.Close -> {
                                    coroutine.launch {
                                        salvageManager.close()
                                    }
                                }

                                SalvageAction.Update -> {
                                    coroutine.launch {
                                        salvageManager.update()
                                    }
                                }

                                is SalvageAction.ChangeName -> {
                                    coroutine.launch {
                                        salvageManager.update {
                                            it.copy(
                                                title = action.name
                                            )
                                        }
                                    }
                                }

                                SalvageAction.Reset -> {
                                    coroutine.launch {
                                        salvageManager.sync()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val textFieldValue = remember(pattern) { pattern.toTextFieldValue() }

            NeoTextField(
                value = syntax
                    .highlight
                    .rememberTextFieldValue(textFieldValue),
                onValueChange = {
                    onAction(
                        FooterAction.UpdateRegex(
                            it.toText()
                        )
                    )
                },
                modifier = Modifier
                    .weight(weight = 1f)
                    .onFocusChanged(onFocus)
                    .onPreviewKeyEvent {
                        when (Command.from(it)) {
                            Command.UNDO -> {
                                onAction(
                                    FooterAction.History.Undo(Field.REGEX)
                                )
                                true
                            }

                            Command.REDO -> {
                                onAction(
                                    FooterAction.History.Redo(Field.REGEX)
                                )
                                true
                            }

                            else -> false
                        }
                    },
                singleLine = true,
                hint = {
                    Text(
                        text = stringResource(Res.string.matcher_footer_insert_regex_hint),
                        style = LocalTextStyle.current.let {
                            it.copy(
                                color = it.color.copy(
                                    alpha = 0.5f
                                )
                            )
                        },
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
            )

            tooling?.let {
                Row(
                    modifier = Modifier
                        .padding(vertical = dimensions.small)
                        .padding(end = dimensions.default)
                ) {
                    it()
                }
            }

            History(
                history = history,
                onAction = onAction,
                modifier = Modifier
                    .padding(vertical = dimensions.small)
                    .padding(end = dimensions.small)
                    .focusProperties {
                        canFocus = false
                    }
            )
        }
    }
}
