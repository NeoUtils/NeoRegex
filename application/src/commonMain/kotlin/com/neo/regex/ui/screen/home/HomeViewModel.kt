package com.neo.regex.ui.screen.home

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.regex.core.domain.Target
import com.neo.regex.core.util.HistoryManager
import com.neo.regex.ui.screen.home.action.HomeAction
import com.neo.regex.ui.screen.home.state.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel : ViewModel() {

    private val targetFlow = MutableStateFlow(Target.REGEX)

    private val histories = mapOf(
        Target.TEXT to HistoryManager(TextFieldValue()),
        Target.REGEX to HistoryManager(TextFieldValue())
    )

    private val inputs = mapOf(
        Target.TEXT to MutableStateFlow(TextFieldValue()),
        Target.REGEX to MutableStateFlow(TextFieldValue())
    )

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
        }
    }

    val uiState = combine(
        checkNotNull(inputs[Target.TEXT]),
        checkNotNull(inputs[Target.REGEX]),
        historyFlow,
    ) { text, regex, history ->
        HomeUiState(
            text,
            regex,
            history
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState()
    )

    fun onAction(action: HomeAction) {

        when (action) {
            is HomeAction.UpdateRegex -> {
                onRegexChange(action.input)
            }

            is HomeAction.History.Undo -> {
                onUndo(target = action.target ?: targetFlow.value)
            }

            is HomeAction.History.Redo -> {
                onRedo(target = action.target ?: targetFlow.value)
            }

            is HomeAction.TargetChange -> {
                targetFlow.value = action.target
            }

            is HomeAction.UpdateText -> {
                onTextChange(action.input)
            }
        }
    }

    private fun onTextChange(input: TextFieldValue) {
        inputs[Target.TEXT]?.value = input
        histories[Target.TEXT]?.snapshot(input)
    }

    private fun onRegexChange(input: TextFieldValue) {
        inputs[Target.REGEX]?.value = input
        histories[Target.REGEX]?.snapshot(input)
    }

    private fun onRedo(target: Target = this.targetFlow.value) {

        val redo = histories[target]?.redo() ?: return

        inputs[target]?.value = redo
    }

    private fun onUndo(target: Target = this.targetFlow.value) {

        val undo = histories[target]?.undo() ?: return

        inputs[target]?.value = undo
    }
}

