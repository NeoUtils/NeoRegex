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

package com.neoutils.neoregex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neoutils.neoregex.core.designsystem.theme.NeoBackground
import com.neoutils.neoregex.core.dispatcher.NavigationManager
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.feature.about.screen.AboutScreen
import com.neoutils.neoregex.feature.about.screen.LibrariesScreen
import com.neoutils.neoregex.feature.matcher.MatcherScreen
import org.koin.compose.koinInject

@Composable
fun App(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject()
) = NeoBackground(modifier) {

    Navigator(
        screen = MatcherScreen(),
    ) { navigator ->

        LaunchedEffect(navigator.lastItem) {
            when (navigator.lastItem) {
                is MatcherScreen -> {
                    navigation.setScreen(Navigation.Screen.Matcher)
                }

                is AboutScreen -> {
                    navigation.setScreen(Navigation.Screen.About)
                }

                is LibrariesScreen -> {
                    navigation.setScreen(Navigation.Screen.Libraries)
                }
            }
        }

        LaunchedEffect(Unit) {
            navigation.event.collect { event ->
                when (event) {
                    Navigation.Event.Matcher -> {
                        navigator.popUntilRoot()
                    }

                    Navigation.Event.About -> {
                        navigator.popUntilRoot()
                        navigator.push(AboutScreen())
                    }

                    Navigation.Event.OnBack -> {
                        navigator.pop()
                    }
                }
            }
        }

        FadeTransition(navigator) {
            it.Content()
        }
    }
}
