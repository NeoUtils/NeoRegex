package com.neo.regex.core.domain.model

import androidx.compose.ui.text.TextRange

data class Input(
    val text: String = "",
    val selection: TextRange = TextRange.Zero
)
