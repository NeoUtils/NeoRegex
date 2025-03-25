/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
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

package com.neoutils.neoregex.feature.matcher

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neoregex.core.common.manager.TextHistoryManager
import com.neoutils.neoregex.core.common.model.*
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(FlowPreview::class)
class MatcherViewModel(
    private val patternStateRepository: PatternStateRepository
) : ScreenModel {

    private val field = MutableStateFlow<Field?>(value = null)

    private val textHistory = TextHistoryManager()
    private val textFlow = MutableStateFlow(TextState())

    private val resultFlow = combine(
        textFlow.map { it.value }.distinctUntilChanged(),
        patternStateRepository.flow.map { it.text.value }.distinctUntilChanged()
    ) { text, pattern ->

        if (pattern.isEmpty()) {
            return@combine MatcherUiState.Result.Success()
        }

        val regex = try {
            Regex(pattern)
        } catch (t: Throwable) {
            return@combine MatcherUiState.Result.Failure(
                error = t.message ?: "Invalid regex pattern"
            )
        }

        val start = Clock.System.now()

        val result = regex.findAll(text)

        val end = Clock.System.now()

        val matches = buildList {
            result.forEachIndexed { index, match ->
                add(
                    Match(
                        text = match.value,
                        range = match.range,
                        groups = match.groupValues.drop(n = 1),
                        number = index.inc(),
                    )
                )
            }
        }

        MatcherUiState.Result.Success(
            matches = matches,
            performance = Performance(
                duration = end - start,
                matches = matches.size
            )
        )
    }

    private val historyFlow = combine(
        field,
        textHistory.flow,
        patternStateRepository.flow
    ) { target, textHistory, pattern ->
        when (target) {
            Field.TEXT -> textHistory
            Field.REGEX -> pattern.history

            null -> HistoryState()
        }
    }

    private val inputFlow = combine(
        field,
        textFlow,
        patternStateRepository.flow,
    ) { target, text, pattern ->
        Inputs(
            field = target,
            text = text,
            regex = pattern.text
        )
    }

    val uiState = combine(
        inputFlow,
        historyFlow,
        resultFlow.debounce {
            when (it) {
                is MatcherUiState.Result.Failure -> ERROR_DELAY
                is MatcherUiState.Result.Success -> 0L
            }
        }
    ) { inputs, history, result ->
        MatcherUiState(
            inputs = inputs,
            history = history,
            result = result,
            performance = when (result) {
                is MatcherUiState.Result.Failure -> Performance()
                is MatcherUiState.Result.Success -> result.performance
            }
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = MatcherUiState()
    )

    init {
        screenModelScope.launch {
            textFlow.collect {
                textHistory.push(it)
            }
        }
    }

    fun onAction(action: FooterAction) {
        when (action) {
            is FooterAction.UpdateRegex -> {
                patternStateRepository.update(action.text)
            }

            is FooterAction.History.Redo -> {
                redo(
                    field = action.field
                        ?: field.value
                        ?: return
                )
            }

            is FooterAction.History.Undo -> {
                undo(
                    field = action.field
                        ?: field.value
                        ?: return
                )
            }
        }
    }

    fun onAction(action: MatcherAction) {
        when (action) {
            is MatcherAction.UpdateText -> {
                textFlow.value = action.text
            }

            is MatcherAction.TargetChange -> {
                field.value = action.field
            }

            is MatcherAction.History.Undo -> {
                undo(
                    field = action.textState
                        ?: field.value
                        ?: return
                )
            }

            is MatcherAction.History.Redo -> {
                redo(
                    field = action.textState
                        ?: field.value
                        ?: return
                )
            }

            MatcherAction.Toggle -> {
                field.value = when (field.value) {
                    Field.TEXT -> Field.REGEX
                    Field.REGEX -> Field.TEXT
                    null -> Field.TEXT
                }
            }
        }
    }

    private fun redo(field: Field) {
        when (field) {
            Field.TEXT -> {
                textFlow.value = textHistory.redo() ?: return
            }

            Field.REGEX -> {
                patternStateRepository.redo()
            }
        }
    }

    private fun undo(field: Field) {
        when (field) {
            Field.TEXT -> {
                textFlow.value = textHistory.undo() ?: return
            }

            Field.REGEX -> {
                patternStateRepository.undo()
            }
        }
    }

    companion object {
        private const val ERROR_DELAY = 500L
    }
}
