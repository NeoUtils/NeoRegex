package com.neo.regex.core.sharedui.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.neo.regex.core.sharedui.model.Match

fun Match.toText(): AnnotatedString {

    val range = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("range: ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
            append("$range")
        }
    }

    if (groups.isEmpty()) {
        return range
    }

    val groups = groups.mapIndexed { index, group ->
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$index: ")
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                append(group)
            }
        }
    }

    val separator = "\u2500".repeat(
        listOf(
            range,
            *groups.toTypedArray()
        ).maxBy {
            it.length
        }.length
    )

    return buildAnnotatedString {
        append(range)
        append("\n")
        append(separator)
        groups.forEach {
            append("\n")
            append(it)
        }
    }
}
