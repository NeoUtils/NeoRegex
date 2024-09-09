package com.neo.regex.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Text

@Composable
fun HeaderNavigator(
    modifier: Modifier = Modifier
) {
    val navDispatcher = LocalNavigationDispatcher.current

    val currentScreenManager = remember { CurrentScreenManager() }

    val currentScreen by currentScreenManager.screen.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        currentScreenManager.screen.collect { screen ->
            when (screen) {
                CurrentScreen.MATCHER -> {
                    navDispatcher.navigateTo(NavigationEvent.Matcher)
                }

                CurrentScreen.ABOUT -> {
                    navDispatcher.navigateTo(NavigationEvent.About)
                }

                CurrentScreen.SETTINGS -> {
                    navDispatcher.navigateTo(NavigationEvent.Settings)
                }
            }
        }
    }

    Dropdown(
        modifier = modifier,
        menuContent = {
            CurrentScreen.entries.forEach {
                selectableItem(
                    selected = currentScreen == it,
                    onClick = {
                        currentScreenManager.navigateTo(it)
                    }
                ) {
                    Text(it.text)
                }
            }
        }
    ) {
        Text(currentScreen.text)
    }
}

enum class CurrentScreen(val text: String) {
    MATCHER("Matcher"),
    ABOUT("About"),
    SETTINGS("Settings");
}

class CurrentScreenManager {

    private val _screen = MutableStateFlow(CurrentScreen.MATCHER)
    val screen = _screen.asStateFlow()

    fun navigateTo(screen: CurrentScreen) {
        _screen.value = screen
    }
}
