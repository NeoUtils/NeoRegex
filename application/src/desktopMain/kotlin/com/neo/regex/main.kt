package com.neo.regex

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.neo.regex.ui.App

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = "NeoRegex",
        state = rememberWindowState(
            position = WindowPosition.Aligned(
                Alignment.Center
            )
        )
    ) {
        App()
    }
}

@Preview
@Composable
fun DefaultPreview() {
    App()
}