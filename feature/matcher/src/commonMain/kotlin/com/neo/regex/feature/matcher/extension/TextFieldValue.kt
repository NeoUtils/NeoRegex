package com.neo.regex.feature.matcher.extension

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.feature.matcher.model.TextState

fun TextFieldValue.toTextState(allowMultiline: Boolean = true) = TextState(
    text = if (allowMultiline) {
        text
    } else {
        text.replace("\n", "")
    },
    selection = selection
)
