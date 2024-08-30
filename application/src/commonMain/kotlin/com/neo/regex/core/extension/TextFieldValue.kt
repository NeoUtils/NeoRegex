package com.neo.regex.core.extension

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.domain.model.Input

fun TextFieldValue.toInput() = Input(
    text = text,
    selection = selection
)
