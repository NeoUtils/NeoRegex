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
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.validator_add_test_case_btn
import com.neoutils.neoregex.core.sharedui.component.Footer
import com.neoutils.neoregex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.feature.validator.component.TestCase
import com.neoutils.neoregex.feature.validator.state.ValidatorUiState
import org.jetbrains.compose.resources.stringResource
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
            verticalArrangement = Arrangement.spacedBy(dimensions.default.m),
            contentPadding = PaddingValues(dimensions.default.m)
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
                    shape = RoundedCornerShape(dimensions.nano.m),
                    color = colorScheme.surfaceContainer,
                    contentColor = colorScheme.onSurface,
                    border = BorderStroke(
                        width = 1.dp,
                        colorScheme.outlineVariant
                    ),
                    onClick = {
                        viewModel.onAction(ValidatorAction.AddTestCase)
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.validator_add_test_case_btn),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensions.default.m)
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
                    Box(Modifier.size(dimensions.large.m)) {
                        when (result) {
                            ValidatorUiState.Result.RUNNING -> {
                                CircularProgressIndicator(
                                    strokeWidth = dimensions.nano.s,
                                    color = colorScheme.onSurface,
                                    modifier = Modifier
                                        .size(dimensions.large.m)
                                        .padding(dimensions.nano.s)
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
