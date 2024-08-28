package com.neo.regex.core.sharedui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LineNumbers(
    count: Int,
    offset: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {

    val lines = remember(count) {
        // TODO(improve): isn't efficient, O(n)
        IntRange(1, count).joinToString(separator = "\n")
    }

    val scrollState = remember(offset) { ScrollState(offset) }

    // TODO(improve): it's not performant for a large number of lines
    BasicText(
        text = lines,
        style = textStyle.copy(
            textAlign = TextAlign.End,
        ),
        modifier = modifier.verticalScroll(
            state = scrollState,
            enabled = false
        ).padding(
            horizontal = 8.dp
        )
    )
}