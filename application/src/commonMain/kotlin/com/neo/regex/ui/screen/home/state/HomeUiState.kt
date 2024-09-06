package com.neo.regex.ui.screen.home.state

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.domain.model.Target
import com.neo.regex.core.sharedui.model.Match

data class HomeUiState(
    val target: Target? = null,
    val text: TextFieldValue = TextFieldValue(),
    val regex: TextFieldValue = TextFieldValue(),
    val history: History = History(),
    val matchResult: MatchResult = MatchResult.Success(listOf()),
) {
    data class History(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )

    sealed class MatchResult {
        data class Success(
            val matches: List<Match>
        ) : MatchResult()

        data class Failure(
            val error: String
        ) : MatchResult()
    }
}

val HomeUiState.MatchResult.matches
    get() = when (this) {
        is HomeUiState.MatchResult.Failure -> listOf()
        is HomeUiState.MatchResult.Success -> matches
    }

val HomeUiState.MatchResult.error
    get() = when (this) {
        is HomeUiState.MatchResult.Failure -> error
        is HomeUiState.MatchResult.Success -> ""
    }

val HomeUiState.MatchResult.isError: Boolean
    get() = when(this) {
        is HomeUiState.MatchResult.Failure -> true
        is HomeUiState.MatchResult.Success -> false
    }