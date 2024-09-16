package com.neo.regex

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.neo.regex.core.common.util.UiMode
import com.neo.regex.core.common.util.isDark
import com.neo.regex.core.common.util.resolve
import com.neo.regex.core.designsystem.theme.NeoTheme
import com.neo.regex.resources.Res
import com.neo.regex.resources.app_name
import com.neo.regex.resources.flavicon
import com.neo.regex.ui.App
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

    Window(
        icon = painterResource(Res.drawable.flavicon),
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        state = rememberWindowState(
            position = WindowPosition.Aligned(
                Alignment.Center
            )
        )
    ) {

        val uiMode = remember { UiMode.resolve() }

        NeoTheme(uiMode.isDark) {
            App()
        }
    }
}