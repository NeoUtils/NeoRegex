package com.neo.regex.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neo.regex.core.designsystem.theme.NeoBackground
import com.neo.regex.feature.matcher.MatcherScreen

@Composable
fun App() = NeoBackground {
    Navigator(MatcherScreen()) { navigator ->
        FadeTransition(navigator) {
            it.Content()
        }
    }
}