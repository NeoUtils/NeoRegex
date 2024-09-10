package com.neo.regex.feature.matcher.state

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.feature.matcher.model.Target

data class MatcherUiState(
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

val MatcherUiState.MatchResult.matches
    get() = when (this) {
        is MatcherUiState.MatchResult.Failure -> listOf()
        is MatcherUiState.MatchResult.Success -> matches
    }

val MatcherUiState.MatchResult.error
    get() = when (this) {
        is MatcherUiState.MatchResult.Failure -> error
        is MatcherUiState.MatchResult.Success -> ""
    }

val MatcherUiState.MatchResult.isError: Boolean
    get() = when(this) {
        is MatcherUiState.MatchResult.Failure -> true
        is MatcherUiState.MatchResult.Success -> false
    }