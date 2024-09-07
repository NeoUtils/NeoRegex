package com.neo.regex

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.application
import com.neo.regex.designsystem.theme.NeoTheme
import com.neo.regex.resources.Res
import com.neo.regex.resources.github
import com.neo.regex.resources.light_theme
import com.neo.regex.ui.App
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle

fun main() = application {

    var isDarkMode by remember { mutableStateOf(false) }

    IntUiTheme(
        theme = JewelTheme.darkThemeDefinition(),
        styling = ComponentStyling.decoratedWindow(
            titleBarStyle = TitleBarStyle.dark(),
            windowStyle = DecoratedWindowStyle.dark()
        )
    ) {
        DecoratedWindow(
            onCloseRequest = ::exitApplication,
        ) {
            TitleBar(Modifier) {

                Row(Modifier.align(Alignment.Start)) {
                    IconButton(
                        onClick = {
                            isDarkMode = !isDarkMode
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.light_theme),
                            contentDescription = null,
                            tint = if (JewelTheme.isDark) {
                                Color.White
                            } else {
                                Color.Black
                            }
                        )
                    }

                    IconButton(
                        onClick = {

                        },

                        ) {
                        Icon(
                            painter = painterResource(Res.drawable.github),
                            contentDescription = null,
                            tint = if (JewelTheme.isDark) {
                                Color.White
                            } else {
                                Color.Black
                            }
                        )
                    }
                }

                Text(
                    text = "Neo Regex",
                    style = TextStyle(fontSize = 14.sp)
                )
            }

            NeoTheme(isDarkMode) {
                App()
            }
        }
    }
}

@Preview
@Composable
private fun DefaultPreview() {
    NeoTheme {
        App()
    }
}

