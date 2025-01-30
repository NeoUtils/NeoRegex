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

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neorefex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.sharedui.extension.toTextFieldValue
import kotlinx.coroutines.flow.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ValidatorViewModel : ScreenModel {

    private val pattern = MutableStateFlow(TextFieldValue())

    private val testCases = MutableStateFlow(listOf(TestCase()))

    private val expanded = MutableStateFlow<Uuid?>(testCases.value.first().uuid)

    val uiState = combine(
        pattern,
        testCases,
        expanded
    ) { pattern, testCases, selected ->
        ValidatorUiState(
            pattern = pattern,
            validations = testCases.map {
                ValidatorUiState.Validation(it)
            },
            expanded = selected
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            pattern = pattern.value,
            validations = emptyList(),
            expanded = null
        )
    )

    fun onAction(action: ValidatorAction) {
        when (action) {
            is ValidatorAction.ExpandedTestCase -> {
                expanded.value = action.uuid
            }

            is ValidatorAction.RemoveTestCase -> {
                testCases.update { testCases ->
                    testCases.filter {
                        it.uuid != action.uuid
                    }
                }
            }

            is ValidatorAction.UpdateTestCase -> {
                testCases.update { testCases ->
                    testCases.map { oldTestCase ->
                        action.newTestCase.takeIf {
                            it.uuid == oldTestCase.uuid
                        } ?: oldTestCase
                    }
                }
            }

            is ValidatorAction.AddTestCase -> {
                testCases.update {
                    it + action.newTestCase
                }

                expanded.value = action.newTestCase.uuid
            }
        }
    }

    fun onAction(action: FooterAction) {
        when (action) {
            is FooterAction.History.Redo -> {
                // TODO: implement later
            }

            is FooterAction.History.Undo -> {
                // TODO: implement later
            }

            is FooterAction.UpdateRegex -> {
                pattern.value = action.textState.toTextFieldValue()
            }
        }
    }
}