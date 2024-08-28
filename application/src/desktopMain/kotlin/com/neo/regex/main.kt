package com.neo.regex

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.neo.regex.resources.Res
import com.neo.regex.resources.app_name
import com.neo.regex.ui.App
import org.jetbrains.compose.resources.stringResource

fun main() = application {

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
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