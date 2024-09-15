package com.neo.regex.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neo.regex.core.designsystem.theme.NeoBackground
import com.neo.regex.feature.matcher.MatcherScreen

@Composable
fun App(
    modifier: Modifier = Modifier
) = NeoBackground(modifier) {
    Navigator(MatcherScreen()) { navigator ->
        FadeTransition(navigator) {
            it.Content()
        }
    }
}