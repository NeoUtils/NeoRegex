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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.neoutils.neorefex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.core.common.model.Inputs
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.component.LinkColor
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.component.Footer
import com.neoutils.neoregex.core.sharedui.model.History
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ValidatorUiState(
    val validations: List<Validation>,
    val pattern: TextFieldValue,
    val expanded: Uuid? = null,
) {
    data class Validation(
        val test: TestCase,
        val result: Result = Result.IDLE,
    ) {
        enum class Result {
            SUCCESS,
            ERROR,
            IDLE
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
class ValidatorScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize()
    ) {

        val viewModel = rememberScreenModel { ValidatorViewModel() }

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(
                items = uiState.validations,
                key = { _, validation -> validation.test.uuid }
            ) { _, validation ->

                val selected = validation.test.uuid == uiState.expanded

                TestCase(
                    test = validation.test,
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
                                validation.test.uuid
                            )
                        )
                    },
                    onDelete = {
                        viewModel.onAction(
                            ValidatorAction.RemoveTestCase(
                                validation.test.uuid
                            )
                        )
                    },
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
                        viewModel.onAction(ValidatorAction.AddTestCase())
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
            onAction = viewModel::onAction,
            history = History(),
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
data class TestCase(
    val title: String? = null,
    val text: String = "",
    val case: Case = Case.MATCH_ANY,
    val uuid: Uuid = Uuid.random()
) {
    enum class Case(val text: String) {
        MATCH_ANY(text = "Match Any"),
        MATCH_ALL(text = "Match All"),
        MATCH_NONE(text = "Match None")
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun TestCase(
    test: TestCase,
    onTestChange: (TestCase) -> Unit,
    onExpanded: () -> Unit,
    onDelete: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = "Enter input"
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
        border = BorderStroke(
            width = 1.dp,
            colorScheme.outlineVariant
        ),
        onClick = onExpanded,
        enabled = !expanded
    ) {
        Column {
            AnimatedVisibility(visible = expanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                ) {
                    NeoTextField(
                        value = test.title.orEmpty(),
                        onValueChange = {
                            onTestChange(
                                test.copy(
                                    title = it.takeIf(String::isNotEmpty)
                                )
                            )
                        },
                        contentPadding = PaddingValues(0.dp),
                        textStyle = typography.labelMedium,
                        hint = "Untitled",
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
                        onDelete = onDelete
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
                    NeoTextField(
                        value = test.text,
                        onValueChange = {
                            onTestChange(
                                test.copy(
                                    text = it
                                )
                            )
                        },
                        contentPadding = contentPadding,
                        textStyle = mergedTextStyle,
                        hint = hint
                    )
                } else {

                    val title = test.title ?: test.text.ifEmpty { "Untitled" }

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
                        container = {}
                    )
                }
            }
        }
    }
}


@Composable
fun Options(
    case: TestCase.Case,
    onCaseChange: (TestCase.Case) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MatchDropDown(
            case = case,
            onChange = {
                onCaseChange(it)
            }
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Icons.TwoTone.Delete,
                contentDescription = null,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun MatchDropDown(
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
