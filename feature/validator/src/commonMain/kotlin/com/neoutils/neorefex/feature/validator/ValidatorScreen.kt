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

package com.neoutils.neorefex.feature.validator

import androidx.compose.animation.*
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExpandCircleDown
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.neoutils.neorefex.feature.validator.action.ValidatorAction
import com.neoutils.neorefex.feature.validator.state.TestCase
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neorefex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.core.designsystem.component.ErrorTooltip
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.component.LinkColor
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.Green
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.component.Footer
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ValidatorScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {

        val viewModel = koinScreenModel<ValidatorViewModel>()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(
                items = uiState.testCases,
                key = { _, testCase -> testCase.uuid }
            ) { _, testCase ->

                val selected = testCase.uuid == uiState.expanded

                TestCase(
                    test = testCase,
                    onTestChange = { newTestCase ->
                        viewModel.onAction(
                            ValidatorAction.UpdateTestCase(
                                newTestCase
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    expanded = selected,
                    onExpanded = {
                        viewModel.onAction(
                            ValidatorAction.ExpandedTestCase(
                                testCase.uuid
                            )
                        )
                    },
                    onDelete = {
                        viewModel.onAction(
                            ValidatorAction.RemoveTestCase(
                                testCase.uuid
                            )
                        )
                    },
                    onClose = {
                        viewModel.onAction(
                            ValidatorAction.CollapseTestCase(
                                testCase.uuid
                            )
                        )
                    },
                    onCopy = {
                        viewModel.onAction(
                            ValidatorAction.Duplicate(
                                testCase.uuid
                            )
                        )
                    }
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = colorScheme.surfaceContainer,
                    contentColor = colorScheme.onSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        colorScheme.outlineVariant
                    ),
                    onClick = {
                        viewModel.onAction(
                            ValidatorAction.AddTestCase()
                        )
                    }
                ) {
                    Text(
                        text = "Add test case",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }
        }

        Footer(
            pattern = uiState.pattern,
            history = uiState.history,
            tooling = {
                AnimatedContent(
                    targetState = Pair(
                        uiState.result,
                        uiState.error
                    ),
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { (result, error) ->
                    when (result) {
                        ValidatorUiState.Result.WAITING -> {
                            Spacer(Modifier.size(24.dp))
                        }

                        ValidatorUiState.Result.RUNNING -> {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = colorScheme.onSurface,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(2.dp)
                            )
                        }

                        ValidatorUiState.Result.SUCCESS -> {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = null,
                                tint = Green
                            )
                        }

                        ValidatorUiState.Result.ERROR -> {
                            if (error != null) {
                                ErrorTooltip(error)
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Cancel,
                                    contentDescription = null,
                                    tint = colorScheme.error
                                )
                            }
                        }
                    }
                }
            },
            onAction = viewModel::onAction
        )
    }
}
