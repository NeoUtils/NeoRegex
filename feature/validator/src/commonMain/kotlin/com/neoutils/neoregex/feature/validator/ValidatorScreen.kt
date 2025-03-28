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

package com.neoutils.neoregex.feature.validator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import com.neoutils.neoregex.core.designsystem.component.ErrorTooltip
import com.neoutils.neoregex.core.sharedui.component.Footer
import com.neoutils.neoregex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.feature.validator.component.TestCase
import com.neoutils.neoregex.feature.validator.state.ValidatorUiState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ValidatorScreen : Screen {

    override val key = Uuid.random().toString()

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
                TestCase(
                    test = testCase,
                    onAction = viewModel::onAction,
                    modifier = Modifier.fillMaxWidth(),
                    expanded = testCase.selected,
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
                    Box(Modifier.size(24.dp)) {
                        when (result) {
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
                                    tint = colorScheme.tertiary
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

                            ValidatorUiState.Result.WAITING -> Unit
                        }
                    }
                }
            },
            onAction = viewModel::onAction
        )
    }
}
