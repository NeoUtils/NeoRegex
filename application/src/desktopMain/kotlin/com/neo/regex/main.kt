package com.neo.regex

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.application
import com.neo.regex.common.Button
import com.neo.regex.common.Buttons
import com.neo.regex.core.data.datasource.PreferencesDataSource
import com.neo.regex.core.data.datastore.dataStore
import com.neo.regex.core.domain.model.Preferences
import com.neo.regex.designsystem.theme.NeoTheme
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import com.neo.regex.designsystem.theme.NeoTheme.fontSizes
import com.neo.regex.resources.*
import com.neo.regex.ui.App
import com.neo.regex.ui.navigation.HeaderNavigator
import com.neo.regex.ui.theme.NeoDesktopTheme
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar

fun main() = application {

    val preferencesDataSource = remember {
        PreferencesDataSource(
            datastore = dataStore
        )
    }

    val preferences by preferencesDataSource.preferencesFlow.collectAsState(
        initial = preferencesDataSource.preferences
    )

    NeoDesktopTheme(preferences.uiMode) {

        DecoratedWindow(onCloseRequest = ::exitApplication) {

            TitleBar {

                HeaderNavigator(
                    modifier = Modifier
                        .padding(dimensions.small)
                        .align(Alignment.Start),
                )

                Text(
                    text = stringResource(Res.string.app_name),
                    style = TextStyle(fontSize = fontSizes.default)
                )

                val scope = rememberCoroutineScope()

                val uriHandler = LocalUriHandler.current

                Buttons(
                    modifier = Modifier.align(Alignment.End),
                    buttons = listOf(
                        Button(
                            icon = painterResource(Res.drawable.github),
                            onClick = {
                                uriHandler.openUri(
                                    uri = "https://github.com/NeoUtils/NeoRegex"
                                )
                            }
                        ),
                        Button(
                            icon = when (preferences.uiMode) {
                                Preferences.UiMode.LIGHT -> {
                                    painterResource(Res.drawable.light_theme)
                                }

                                Preferences.UiMode.DARK -> {
                                    painterResource(Res.drawable.dark_theme)
                                }
                            },
                            onClick = {
                                scope.launch {
                                    preferencesDataSource.update {
                                        it.copy(
                                            uiMode = it.uiMode.toggle()
                                        )
                                    }
                                }
                            }
                        )
                    )
                )
            }

            App()
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

