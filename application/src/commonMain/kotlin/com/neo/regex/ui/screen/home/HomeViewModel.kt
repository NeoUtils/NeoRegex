package com.neo.regex.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neo.regex.core.domain.model.Input
import com.neo.regex.core.domain.model.Target
import com.neo.regex.core.domain.model.targeted
import com.neo.regex.core.extension.toTextFieldValue
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.core.util.HistoryManager
import com.neo.regex.ui.screen.home.action.HomeAction
import com.neo.regex.ui.screen.home.state.HomeUiState
import kotlinx.coroutines.flow.*

class HomeViewModel : ViewModel() {

    private val targetFlow = MutableStateFlow<Target?>(value = null)

    private val histories = targeted<HistoryManager<Input>>(
        Target.TEXT to HistoryManager(),
        Target.REGEX to HistoryManager()
    )

    private val inputs = targeted(
        Target.TEXT to MutableStateFlow(Input()),
        Target.REGEX to MutableStateFlow(Input())
    )

    private val matches = combine(
        inputs[Target.TEXT].map { it.text },
        inputs[Target.REGEX].map { it.text }
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
        histories[Target.TEXT].state,
        histories[Target.REGEX].state
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
        inputs[Target.TEXT],
        inputs[Target.REGEX],
        historyFlow,
        matches
    ) { text, regex, history, matches ->
        HomeUiState(
            text = text.toTextFieldValue(),
            regex = regex.toTextFieldValue(),
            history = history,
            matches = matches
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeUiState()
    )

    init {
        inputs[Target.TEXT].onEach {
            histories[Target.TEXT].push(it)
        }.launchIn(viewModelScope)

        inputs[Target.REGEX].onEach {
            histories[Target.REGEX].push(it)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HomeAction) {

        when (action) {
            is HomeAction.Input.UpdateRegex -> {
                onChange(Target.REGEX, action.input)
            }

            is HomeAction.Input.UpdateText -> {
                onChange(Target.TEXT, action.input)
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

    private fun onChange(target: Target, input: Input) {
        histories[target].unlock()
        inputs[target].value = input
    }

    private fun onRedo(target: Target) {
        inputs[target].value = histories[target].redo() ?: return
    }

    private fun onUndo(target: Target) {
        inputs[target].value = histories[target].undo() ?: return
    }
}

