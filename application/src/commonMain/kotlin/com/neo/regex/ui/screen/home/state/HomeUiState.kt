package com.neo.regex.ui.screen.home.state

import com.neo.regex.core.sharedui.model.Match

data class HomeUiState(
    val text: String = "",
    val regex: String = "",
    val history: History = History(),
    val matches: List<Match> = listOf(),
) {
    data class History(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )
}