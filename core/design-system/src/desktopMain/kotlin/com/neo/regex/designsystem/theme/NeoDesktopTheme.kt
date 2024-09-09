package com.neo.regex.designsystem.theme

import androidx.compose.runtime.Composable
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle

@Composable
fun NeoDesktopTheme(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {

    val jewelTheme = if (darkMode) {
        JewelTheme.darkThemeDefinition()
    } else {
        JewelTheme.lightThemeDefinition()
    }

    val jewelStyling = if (darkMode) {
        ComponentStyling.default().decoratedWindow(
            titleBarStyle = TitleBarStyle.dark(),
            windowStyle = DecoratedWindowStyle.dark()
        )
    } else {
        ComponentStyling.default().decoratedWindow(
            titleBarStyle = TitleBarStyle.lightWithLightHeader(),
            windowStyle = DecoratedWindowStyle.light()
        )
    }

    IntUiTheme(
        theme = jewelTheme,
        styling = jewelStyling
    ) {
        NeoTheme(darkMode = JewelTheme.isDark) {
            content()
        }
    }
}