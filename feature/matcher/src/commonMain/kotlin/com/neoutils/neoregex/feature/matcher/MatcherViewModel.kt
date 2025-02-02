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
import com.neoutils.neoregex.core.common.model.History
import com.neoutils.neoregex.core.common.model.Inputs
import com.neoutils.neoregex.core.common.model.Target
import com.neoutils.neoregex.core.common.model.Text
import com.neoutils.neoregex.core.repository.pattern.PatternRepository
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.core.sharedui.model.Match
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

@OptIn(FlowPreview::class)
class MatcherViewModel(
    private val patternRepository: PatternRepository
) : ScreenModel {

    private val target = MutableStateFlow<Target?>(value = null)

    private val textHistory = TextHistoryManager()
    private val textFlow = MutableStateFlow(Text())

    private val resultFlow = combine(
        textFlow.map { it.text }.distinctUntilChanged(),
        patternRepository.flow.map { it.text }.distinctUntilChanged()
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
        target,
        textHistory.flow,
        patternRepository.historyFlow
    ) { target, textHistory, regexHistory ->
        when (target) {
            Target.TEXT -> textHistory
            Target.REGEX -> regexHistory

            null -> History()
        }
    }

    private val inputFlow = combine(
        target,
        textFlow,
        patternRepository.flow,
    ) { target, text, regex ->
        Inputs(
            target = target,
            text = text,
            regex = regex
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
        textFlow.onEach {
            textHistory.push(it)
        }.launchIn(screenModelScope)
    }

    fun onAction(action: FooterAction) {
        when (action) {
            is FooterAction.UpdateRegex -> {
                patternRepository.update(action.text)
            }

            is FooterAction.History.Redo -> {
                redo(
                    target = action.textState
                        ?: target.value
                        ?: return
                )
            }

            is FooterAction.History.Undo -> {
                undo(
                    target = action.textState
                        ?: target.value
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
                target.value = action.target
            }

            is MatcherAction.History.Undo -> {
                undo(
                    target = action.textState
                        ?: target.value
                        ?: return
                )
            }

            is MatcherAction.History.Redo -> {
                redo(
                    target = action.textState
                        ?: target.value
                        ?: return
                )
            }

            MatcherAction.Toggle -> {
                target.value = when (target.value) {
                    Target.TEXT -> Target.REGEX
                    Target.REGEX -> Target.TEXT
                    null -> Target.TEXT
                }
            }
        }
    }

    private fun redo(target: Target) {
        when (target) {
            Target.TEXT -> {
                textFlow.value = textHistory.redo() ?: return
            }

            Target.REGEX -> {
                patternRepository.redo()
            }
        }
    }

    private fun undo(target: Target) {
        when (target) {
            Target.TEXT -> {
                textFlow.value = textHistory.undo() ?: return
            }

            Target.REGEX -> {
                patternRepository.undo()
            }
        }
    }

    companion object {
        private const val ERROR_DELAY = 500L
    }
}
