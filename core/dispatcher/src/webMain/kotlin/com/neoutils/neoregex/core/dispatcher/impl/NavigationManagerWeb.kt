/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.dispatcher.impl

import com.neoutils.neoregex.core.dispatcher.NavigationManager
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class NavigationManagerWeb : NavigationManager {

    private val coroutines = CoroutineScope(Dispatchers.Main)

    private val _event = Channel<Navigation.Event>(Channel.UNLIMITED)
    private val _current = MutableStateFlow<Navigation.Screen>(Navigation.Screen.Matcher)
    private val _canPop = MutableStateFlow(value = false)

    override val screen = _current.asStateFlow()
    override val event = _event.receiveAsFlow()
    override val canPopBack = _canPop.asStateFlow()

    private var stack = 0

    init {
        window.onpopstate = { _ ->
            coroutines.launch {
                navigate(window.location.search)
            }
        }

        window.history.replaceState(
            data = null,
            title = "",
            url = "?screen=matcher"
        )

        coroutines.launch {
            navigate(window.location.search)
        }
    }

    override fun update(screen: Navigation.Screen) {
        _canPop.value = when (screen) {
            Navigation.Screen.About -> true
            Navigation.Screen.Libraries -> true
            Navigation.Screen.Matcher -> false
            Navigation.Screen.Validator -> false
        }

        _current.value = screen
    }

    override suspend fun emit(event: Navigation.Event) {
        _event.send(event)

        if (event is Navigation.Event.Navigate) {
            registerHistory(event.screen)
        }
    }

    private fun registerHistory(screen: Navigation.Screen) {
        when (screen) {
            Navigation.Screen.Matcher -> {
                window.history.go(delta = -stack)
                stack = 0
            }

            Navigation.Screen.About -> {
                if (screen is Navigation.Screen.Libraries) {
                    window.history.back()
                    return
                }

                window.history.pushState(
                    data = null,
                    title = "",
                    url = "?screen=about"
                )
                stack = 1
            }

            Navigation.Screen.Validator -> {
                window.history.pushState(
                    data = null,
                    title = "",
                    url = "?screen=validator"
                )

                stack = 1
            }

            Navigation.Screen.Libraries -> {
                window.history.pushState(
                    data = null,
                    title = "",
                    url = "?screen=libraries"
                )
                stack = 2
            }

        }
    }

    private suspend fun navigate(query: String) {
        val result = ScreenArg.find(query) ?: return

        val screen = result.groups[1]?.value

        when (screen) {
            "about" -> {
                _event.send(
                    Navigation.Event.Navigate(
                        Navigation.Screen.About
                    )
                )
            }

            "libraries" -> {
                _event.send(
                    Navigation.Event.Navigate(
                        Navigation.Screen.Libraries
                    )
                )
            }

            "matcher" -> {
                _event.send(
                    Navigation.Event.Navigate(
                        Navigation.Screen.Matcher
                    )
                )
            }

            "validator" -> {
                _event.send(
                    Navigation.Event.Navigate(
                        Navigation.Screen.Validator
                    )
                )
            }
        }
    }

    companion object {
        private val ScreenArg = "\\?screen=(\\w+)".toRegex()
    }
}
