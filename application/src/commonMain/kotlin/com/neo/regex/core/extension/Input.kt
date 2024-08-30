package com.neo.regex.core.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.core.domain.model.Input

fun Input.toTextFieldValue(
    spanStyles: List<AnnotatedString.Range<SpanStyle>> = listOf()
) = TextFieldValue(
    annotatedString =  AnnotatedString(
        text = text,
        spanStyles = spanStyles
    ),
    selection = selection,
    composition = null
)
