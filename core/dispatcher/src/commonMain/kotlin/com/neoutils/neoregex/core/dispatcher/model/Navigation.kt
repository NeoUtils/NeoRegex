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

package com.neoutils.neoregex.core.dispatcher.model

import com.neoutils.neoregex.core.resources.*
import org.jetbrains.compose.resources.StringResource

sealed class Navigation {

    sealed class Event : Navigation() {
        data class Navigate(
            val screen: Screen
        ) : Event()

        data object OnBack : Event()
    }

    sealed class Screen : Navigation() {

        abstract val title: StringResource

        data object Matcher : Screen() {
            override val title = Res.string.screen_matcher
        }

        data object About : Screen() {
            override val title = Res.string.screen_about
        }

        data object Libraries : Screen() {
            override val title = Res.string.screen_libraries
        }

        data object Validator : Screen() {
            override val title = Res.string.screen_validator
        }

        data object Saved : Screen() {
            override val title = Res.string.screen_saved
        }
    }
}
