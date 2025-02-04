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

package com.neoutils.neoregex.feature.validator.state

import androidx.compose.animation.*
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ExpandCircleDown
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.util.ObservableMutableMap
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.component.LinkColor
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.Green
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.feature.validator.model.TestResult
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TestCaseUi(
    val uuid: Uuid,
    val title: String,
    val text: String,
    val case: TestCase.Case,
    val result: TestResult,
    val selected: Boolean
)

@OptIn(ExperimentalUuidApi::class)
fun List<TestCase>.toTestCaseUi(
    results: Map<Uuid, TestResult>,
    expanded: Uuid?
): List<TestCaseUi> = map { testCase ->
    TestCaseUi(
        uuid = testCase.uuid,
        title = testCase.title,
        text = testCase.text,
        case = testCase.case,
        result = results[testCase.uuid] ?: TestResult.IDLE,
        selected = expanded == testCase.uuid
    )
}

@OptIn(ExperimentalUuidApi::class)
sealed class TestCaseAction {
    data class ChangeTitle(
        val uuid: Uuid,
        val title: String
    ) : TestCaseAction()

    data class ChangeText(
        val uuid: Uuid,
        val text: String
    ) : TestCaseAction()

    data class ChangeCase(
        val uuid: Uuid,
        val case: TestCase.Case
    ) : TestCaseAction()

    data class Delete(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Collapse(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Expanded(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Duplicate(
        val uuid: Uuid,
    ) : TestCaseAction()
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalUuidApi::class
)
@Composable
fun TestCase(
    test: TestCaseUi,
    expanded: Boolean,
    onAction: (TestCaseAction) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = "Enter input"
) {
    val color by animateColorAsState(
        when (test.result) {
            TestResult.IDLE -> colorScheme.outlineVariant

            TestResult.RUNNING -> {
                rememberInfiniteTransition().animateColor(
                    initialValue = colorScheme.outlineVariant,
                    targetValue = Color.White,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000),
                        repeatMode = RepeatMode.Reverse
                    )
                ).value
            }

            TestResult.SUCCESS -> Green
            TestResult.ERROR -> colorScheme.error
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
                            onAction(
                                TestCaseAction.ChangeTitle(
                                    uuid = test.uuid,
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
                            onAction(
                                TestCaseAction.ChangeCase(
                                    uuid = test.uuid,
                                    case = it
                                )
                            )
                        },
                        onDelete = {
                            onAction(
                                TestCaseAction.Delete(test.uuid)
                            )
                        },
                        onClose = {
                            onAction(
                                TestCaseAction.Collapse(test.uuid)
                            )
                        },
                        onDuplicate = {
                            onAction(
                                TestCaseAction.Duplicate(test.uuid)
                            )
                        }
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
                            onAction(
                                TestCaseAction.ChangeText(
                                    uuid = test.uuid,
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
                                    onClick = {
                                        onAction(
                                            TestCaseAction.Expanded(test.uuid)
                                        )
                                    }
                                )
                            )
                        },
                        trailingIcon = {
                            Text(
                                text = when (test.case) {
                                    TestCase.Case.MATCH_ANY -> "Match Any"
                                    TestCase.Case.MATCH_ALL -> "Match All"
                                    TestCase.Case.MATCH_NONE -> "Match None"
                                },
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
    onDuplicate: () -> Unit = {},
) = Row(
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
        onClick = onDuplicate,
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
            modifier = Modifier
                .rotate(degrees = 180f)
                .padding(4.dp)
        )
    }
}

@Composable
private fun MatchDropDown(
    case: TestCase.Case,
    onChange: (TestCase.Case) -> Unit
) = Column(verticalArrangement = Arrangement.Center) {

    val expanded = remember { mutableStateOf(false) }

    Link(
        text = when (case) {
            TestCase.Case.MATCH_ANY -> "Match Any"
            TestCase.Case.MATCH_ALL -> "Match All"
            TestCase.Case.MATCH_NONE -> "Match None"
        },
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
                        text = when (it) {
                            TestCase.Case.MATCH_ANY -> "Match Any"
                            TestCase.Case.MATCH_ALL -> "Match All"
                            TestCase.Case.MATCH_NONE -> "Match None"
                        },
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