package com.neo.regex.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.regex.core.domain.model.Target
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.core.util.HistoryManager
import com.neo.regex.ui.screen.home.action.HomeAction
import com.neo.regex.ui.screen.home.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {

    private val targetFlow = MutableStateFlow<Target?>(value = null)

    private val histories = mapOf(
        Target.TEXT to HistoryManager(""),
        Target.REGEX to HistoryManager("")
    )

    private val inputs = mapOf(
        Target.TEXT to MutableStateFlow(""),
        Target.REGEX to MutableStateFlow("")
    )

    private val matches = combine(
        checkNotNull(inputs[Target.TEXT]),
        checkNotNull(inputs[Target.REGEX])
    ) { text, pattern ->

        val regex = runCatching {
            Regex(pattern)
        }.getOrNull()

        buildList {
            regex?.findAll(text)?.forEach {
                if (it.value.isNotEmpty()) {
                    add(
                        Match(
                            start = it.range.first,
                            end = it.range.last + 1
                        )
                    )
                }
            }
        }
    }

    private val historyFlow = combine(
        targetFlow,
        checkNotNull(histories[Target.TEXT]).state,
        checkNotNull(histories[Target.REGEX]).state
    ) { target, text, regex ->
        when (target) {
            Target.TEXT -> {
                HomeUiState.History(
                    canUndo = text.canUndo,
                    canRedo = text.canRedo
                )
            }

            Target.REGEX -> {
                HomeUiState.History(
                    canUndo = regex.canUndo,
                    canRedo = regex.canRedo
                )
            }

            null -> HomeUiState.History()
        }
    }

    val uiState = combine(
        checkNotNull(inputs[Target.TEXT]),
        checkNotNull(inputs[Target.REGEX]),
        historyFlow,
        matches
    ) { text, regex, history, matches ->
        HomeUiState(
            text = text,
            regex = regex,
            history = history,
            matches = matches
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState()
    )

    fun onAction(action: HomeAction) {

        when (action) {
            is HomeAction.Input.UpdateRegex -> {
                onRegexChange(action.input)
            }

            is HomeAction.Input.UpdateText -> {
                onTextChange(action.input)
            }

            is HomeAction.History.Undo -> {
                onUndo(
                    target = action.target
                        ?: targetFlow.value
                        ?: return
                )
            }

            is HomeAction.History.Redo -> {
                onRedo(
                    target = action.target
                        ?: targetFlow.value
                        ?: return
                )
            }

            is HomeAction.TargetChange -> {
                targetFlow.value = action.target
            }

        }
    }

    private fun onTextChange(input: String) {
        inputs[Target.TEXT]?.value = input
        histories[Target.TEXT]?.snapshot(input)
    }

    private fun onRegexChange(input: String) {
        inputs[Target.REGEX]?.value = input
        histories[Target.REGEX]?.snapshot(input)
    }

    private fun onRedo(target: Target) {

        val redo = histories[target]?.redo() ?: return

        inputs[target]?.value = redo
    }

    private fun onUndo(target: Target) {

        val undo = histories[target]?.undo() ?: return

        inputs[target]?.value = undo
    }
}

