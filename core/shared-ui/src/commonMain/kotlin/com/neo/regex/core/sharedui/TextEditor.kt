package com.neo.regex.core.sharedui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

data class Match(
    val start: Int,
    val end: Int
)

@Composable
expect fun TextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChange: (FocusState) -> Unit = {},
    matches: List<Match> = listOf(),
    textStyle: TextStyle = TextStyle(),
)