package com.neo.regex.ui

import androidx.compose.runtime.Composable
import com.neo.regex.core.domain.model.Preferences
import com.neo.regex.designsystem.theme.NeoTheme
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
    uiMode: Preferences.UiMode,
    content: @Composable () -> Unit
) {

    val jewelTheme = when (uiMode) {
       Preferences.UiMode.LIGHT -> JewelTheme.lightThemeDefinition()
       Preferences.UiMode.DARK -> JewelTheme.darkThemeDefinition()
    }

    val jewelStyling = when (uiMode) {
        Preferences.UiMode.LIGHT -> {
            ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.lightWithLightHeader(),
                windowStyle = DecoratedWindowStyle.light()
            )
        }

        Preferences.UiMode.DARK -> {
            ComponentStyling.default().decoratedWindow(
                titleBarStyle = TitleBarStyle.dark(),
                windowStyle = DecoratedWindowStyle.dark()
            )
        }
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