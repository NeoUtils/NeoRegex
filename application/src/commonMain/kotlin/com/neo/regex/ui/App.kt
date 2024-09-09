package com.neo.regex.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neo.regex.designsystem.theme.NeoBackground
import com.neo.regex.ui.screen.home.HomeScreen

@Composable
fun App() = NeoBackground {
    Navigator(HomeScreen()) { navigator ->
        FadeTransition(navigator) {
            it.Content()
        }
    }
}