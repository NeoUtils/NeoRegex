package com.neo.regex.ui

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.neo.regex.design.theme.NeoTheme

@Composable
fun App() = NeoTheme(darkMode = false) {

    var text by remember { mutableStateOf(TextFieldValue()) }

    TextEditor(
        value = text,
        onValueChange = {
            text = it
        },
    )
}