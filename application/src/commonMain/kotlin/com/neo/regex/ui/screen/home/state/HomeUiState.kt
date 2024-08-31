package com.neo.regex.ui.screen.home.state

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.sharedui.model.Match

data class HomeUiState(
    val text: TextFieldValue = TextFieldValue(),
    val regex: TextFieldValue = TextFieldValue(),
    val history: History = History(),
    val matches: List<Match> = listOf(),
) {
    data class History(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )
}