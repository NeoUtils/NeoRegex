package com.neo.regex.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neo.regex.designsystem.theme.NeoBackground
import com.neo.regex.ui.navigation.LocalNavigationDispatcher
import com.neo.regex.ui.navigation.NavigationEvent
import com.neo.regex.ui.screen.about.AboutScreen
import com.neo.regex.ui.screen.home.HomeScreen
import com.neo.regex.ui.screen.setting.SettingScreen

@Composable
fun App() = NeoBackground {
    Navigator(HomeScreen()) { navigator ->

        val navDispatcher = LocalNavigationDispatcher.current

        LaunchedEffect(Unit) {
            navDispatcher.events.collect { route ->
                when (route) {
                    NavigationEvent.Matcher -> {
                        navigator.popUntilRoot()
                    }

                    NavigationEvent.About -> {
                        if (navigator.lastItem !is AboutScreen) {
                            navigator.replaceAll(
                                items = navigator.items.dropLast(
                                    navigator.items.lastIndex
                                ) + AboutScreen()
                            )
                        }
                    }

                    NavigationEvent.Settings -> {
                        if (navigator.lastItem !is SettingScreen) {
                            navigator.replaceAll(
                                items = navigator.items.dropLast(
                                    navigator.items.lastIndex
                                ) + SettingScreen()
                            )
                        }
                    }
                }
            }
        }

        FadeTransition(navigator) {
            it.Content()
        }
    }
}