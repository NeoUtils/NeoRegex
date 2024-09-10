package com.neo.regex.feature.matcher.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

fun String.withSpanStyles(
    spanStyles: List<AnnotatedString.Range<SpanStyle>>
): AnnotatedString {
    return AnnotatedString(
        text = this,
        spanStyles = spanStyles.filter {
            it.start <= length && it.end <= length
        }
    )
}