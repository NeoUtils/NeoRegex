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
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.core.sharedui.model.Match
import com.neoutils.neoregex.feature.matcher.action.MatcherAction
import com.neoutils.neoregex.feature.matcher.extension.toTextFieldValue
import com.neoutils.neoregex.feature.matcher.manager.HistoryManager
import com.neoutils.neoregex.feature.matcher.model.Target
import com.neoutils.neoregex.feature.matcher.model.Targeted
import com.neoutils.neoregex.feature.matcher.model.TextState
import com.neoutils.neoregex.feature.matcher.state.MatcherUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock

@OptIn(FlowPreview::class)
class MatcherViewModel : ScreenModel {

    private val target = MutableStateFlow<Target?>(value = null)

    private val histories = Targeted(
        Target.TEXT to HistoryManager(),
        Target.REGEX to HistoryManager()
    )

    private val inputs = Targeted(
        Target.TEXT to MutableStateFlow(TextState()),
        Target.REGEX to MutableStateFlow(TextState())
    )

    private val resultFlow = combine(
        inputs[Target.TEXT].map { it.text }.distinctUntilChanged(),
        inputs[Target.REGEX].map { it.text }.distinctUntilChanged()
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
        histories[Target.TEXT].state,
        histories[Target.REGEX].state
    ) { target, text, regex ->
        when (target) {
            Target.TEXT -> {
                MatcherUiState.History(
                    canUndo = text.canUndo,
                    canRedo = text.canRedo
                )
            }

            Target.REGEX -> {
                MatcherUiState.History(
                    canUndo = regex.canUndo,
                    canRedo = regex.canRedo
                )
            }

            null -> MatcherUiState.History()
        }
    }

    private val inputFlow = combine(
        target,
        inputs[Target.TEXT],
        inputs[Target.REGEX],
    ) { target, text, regex ->
        MatcherUiState.Inputs(
            target = target,
            text = text.toTextFieldValue(),
            regex = regex.toTextFieldValue()
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
        inputs[Target.TEXT]
            .filter { it.register }
            .onEach { histories[Target.TEXT].push(it) }
            .launchIn(screenModelScope)

        inputs[Target.REGEX]
            .filter { it.register }
            .onEach { histories[Target.REGEX].push(it) }
            .launchIn(screenModelScope)
    }

    fun onAction(action: MatcherAction) {

        when (action) {
            is MatcherAction.Input.UpdateRegex -> {
                onChange(Target.REGEX, action.textState)
            }

            is MatcherAction.Input.UpdateText -> {
                onChange(Target.TEXT, action.textState)
            }

            is MatcherAction.History.Undo -> {
                onUndo(
                    target = action.textState
                        ?: target.value
                        ?: return
                )
            }

            is MatcherAction.History.Redo -> {
                onRedo(
                    target = action.textState
                        ?: target.value
                        ?: return
                )
            }

            is MatcherAction.TargetChange -> {
                target.value = action.target
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

    private fun onChange(
        target: Target,
        textState: TextState
    ) {
        inputs[target].value = textState
    }

    private fun onRedo(target: Target) {
        val redo = histories[target].redo()

        if (redo != null) {
            inputs[target].value = redo.copy(register = false)
        }
    }

    private fun onUndo(target: Target) {
        val undo = histories[target].undo()

        if (undo != null) {
            inputs[target].value = undo.copy(register = false)
        }
    }

    companion object {
        private const val ERROR_DELAY = 500L
    }
}
