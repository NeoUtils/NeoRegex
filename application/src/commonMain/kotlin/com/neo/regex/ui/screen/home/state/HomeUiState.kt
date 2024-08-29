package com.neo.regex.ui.screen.home.state

import androidx.compose.ui.text.input.TextFieldValue

data class HomeUiState(
    val text: TextFieldValue = TextFieldValue(),
    val regex: TextFieldValue = TextFieldValue(),
    val history: History = History(),
) {
    data class History(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )
}