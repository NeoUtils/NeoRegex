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

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalUuidApi::class
)
@Composable
private fun TestCase(
    test: TestCase,
    expanded: Boolean,
    onTestChange: (TestCase) -> Unit,
    modifier: Modifier = Modifier,
    onExpanded: () -> Unit = {},
    onClose: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCopy: () -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = "Enter input"
) {
    val color by animateColorAsState(
        when (test.result) {
            TestCase.Result.IDLE -> colorScheme.outlineVariant

            TestCase.Result.RUNNING -> {
                rememberInfiniteTransition().animateColor(
                    initialValue = colorScheme.outlineVariant,
                    targetValue = Color.White,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000),
                        repeatMode = RepeatMode.Reverse
                    )
                ).value
            }

            TestCase.Result.SUCCESS -> Green
            TestCase.Result.ERROR -> colorScheme.error
        }
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            color = color
        ),
    ) {
        Column {
            AnimatedVisibility(visible = expanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimensions.small),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                ) {
                    NeoTextField(
                        value = test.title,
                        onValueChange = {
                            onTestChange(
                                test.copy(
                                    title = it
                                )
                            )
                        },
                        contentPadding = PaddingValues(0.dp),
                        textStyle = typography.labelMedium,
                        hint = "Untitled",
                        singleLine = true,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    Options(
                        case = test.case,
                        onCaseChange = {
                            onTestChange(
                                test.copy(
                                    case = it
                                )
                            )
                        },
                        onDelete = onDelete,
                        onClose = onClose,
                        onCopy = onCopy
                    )
                }
            }

            val mergedTextStyle = typography.bodyLarge.merge(textStyle)

            AnimatedContent(
                targetState = expanded,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { expanded ->
                if (expanded) {

                    val focusRequester = remember { FocusRequester() }

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }

                    NeoTextField(
                        value = test.text,
                        onValueChange = {
                            onTestChange(
                                test.copy(
                                    text = it
                                )
                            )
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth(),
                        contentPadding = contentPadding,
                        textStyle = mergedTextStyle,
                        hint = hint
                    )
                } else {

                    val title = test.title.ifEmpty {
                        test.text.ifEmpty {
                            "Untitled"
                        }
                    }

                    TextFieldDefaults.DecorationBox(
                        value = title,
                        innerTextField = {
                            Text(
                                text = title,
                                modifier = Modifier.fillMaxWidth(),
                                style = mergedTextStyle,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        },
                        enabled = true,
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        interactionSource = remember { MutableInteractionSource() },
                        contentPadding = contentPadding,
                        container = {
                            Box(
                                modifier.clickable(
                                    onClick = onExpanded
                                )
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = test.case.text,
                                modifier = Modifier.padding(contentPadding),
                                style = typography.labelMedium
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Options(
    case: TestCase.Case,
    onCaseChange: (TestCase.Case) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onClose: () -> Unit = {},
    onCopy: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimensions.tiny)
    ) {
        MatchDropDown(
            case = case,
            onChange = {
                onCaseChange(it)
            }
        )

        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = null,
                modifier = Modifier
                    .padding(vertical = 0.5.dp)
                    .padding(4.dp)
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ExpandCircleDown,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun MatchDropDown(
    case: TestCase.Case,
    onChange: (TestCase.Case) -> Unit
) {
    Column(verticalArrangement = Arrangement.Center) {

        val expanded = remember { mutableStateOf(false) }

        Link(
            text = case.text,
            onClick = {
                expanded.value = true
            },
            enabledUnderline = false,
            colors = LinkColor(
                idle = colorScheme.onSurface,
                pressed = colorScheme.onSurface,
                press = colorScheme.onSurface.copy(alpha = 0.6f),
                hover = colorScheme.onSurface.copy(alpha = 0.8f)
            ),
            endIcon = {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    modifier = Modifier.size(18.dp),
                    contentDescription = null
                )
            },
            style = typography.labelMedium
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            TestCase.Case.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = it.text,
                        )
                    },
                    onClick = {
                        expanded.value = false
                        onChange(it)
                    },
                )
            }
        }
    }
}
