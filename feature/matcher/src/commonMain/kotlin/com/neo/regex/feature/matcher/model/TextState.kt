package com.neo.regex.feature.matcher.model

import androidx.compose.ui.text.TextRange

data class TextState(
    val text: String = "",
    val selection: TextRange = TextRange.Zero
)
