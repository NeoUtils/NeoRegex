package com.neo.regex.feature.matcher.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.feature.matcher.model.TextState

fun TextState.toTextFieldValue(
    spanStyles: List<AnnotatedString.Range<SpanStyle>> = listOf()
) = TextFieldValue(
    annotatedString = text.withSpanStyles(spanStyles),
    selection = selection,
    composition = null
)
