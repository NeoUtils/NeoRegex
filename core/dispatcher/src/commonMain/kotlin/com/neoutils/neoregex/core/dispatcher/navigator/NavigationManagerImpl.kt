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

package com.neoutils.neoregex.core.dispatcher.navigator

import com.neoutils.neoregex.core.dispatcher.model.Navigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class NavigationManagerImpl : NavigationManager {

    private val _event = Channel<Navigation.Event>(Channel.UNLIMITED)
    private val _screen = MutableStateFlow<Navigation.Screen>(Navigation.Screen.Matcher)
    private val _canPopBack = MutableStateFlow(value = false)

    override val screen = _screen.asStateFlow()
    override val event = _event.receiveAsFlow()
    override val canPopBack = _canPopBack.asStateFlow()

    override fun update(screen: Navigation.Screen) {
        _canPopBack.value = when (screen) {
            Navigation.Screen.About -> true
            Navigation.Screen.Libraries -> true
            Navigation.Screen.Matcher -> false
            Navigation.Screen.Validator -> false
        }

        _screen.value = screen
    }

    override suspend fun emit(event: Navigation.Event) {
        if (event is Navigation.Event.OnBack && !canPopBack.value) return
        _event.send(event)
    }
}