/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
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

import com.neoutils.neoregex.core.dispatcher.NavigationDispatcher
import com.neoutils.neoregex.core.dispatcher.event.Navigation
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal class NavigationDispatcherImpl : NavigationDispatcher {

    private val _flow = Channel<Navigation>(Channel.UNLIMITED)
    private val _current = MutableStateFlow<Navigation>(Navigation.Matcher)

    override val flow: Flow<Navigation>
        get() = _flow.receiveAsFlow()

    override val current: Navigation
        get() = _current.value

    override suspend fun emit(navigation: Navigation) {
        _current.value = navigation
        _flow.send(navigation)
    }
}