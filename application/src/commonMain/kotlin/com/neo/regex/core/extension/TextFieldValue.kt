package com.neo.regex.core.extension

import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.domain.model.Input

fun TextFieldValue.toInput(allowMultiline: Boolean = true) = Input(
    text =  if (allowMultiline) {
        text
    } else {
        text.replace("\n", "")
    },
    selection = selection
)
