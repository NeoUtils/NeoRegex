package com.neo.regex.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

val LocalNavigationDispatcher = staticCompositionLocalOf {
    NavigationDispatcher()
}

sealed class NavigationEvent {
    data object Matcher : NavigationEvent()
    data object About : NavigationEvent()
    data object Settings : NavigationEvent()
}

class NavigationDispatcher {
    private val _events = Channel<NavigationEvent>(Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    suspend fun navigateTo(event: NavigationEvent) {
        _events.send(event)
    }
}