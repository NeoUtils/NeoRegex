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

@file:OptIn(InternalVoyagerApi::class)

package com.neoutils.neoregex

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.koin.koinNavigatorScreenModel
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.internal.BackHandler
import cafe.adriel.voyager.transitions.FadeTransition
import com.neoutils.neoregex.core.designsystem.theme.NeoBackground
import com.neoutils.neoregex.core.dispatcher.control.Controller
import com.neoutils.neoregex.core.dispatcher.event.Command
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import com.neoutils.neoregex.feature.about.screen.AboutScreen
import com.neoutils.neoregex.feature.about.screen.LibrariesScreen
import com.neoutils.neoregex.feature.matcher.MatcherScreen
import com.neoutils.neoregex.feature.validator.ValidatorScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun App(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    controller: Controller = koinInject()
) = NeoBackground(modifier) {

    val coroutine = rememberCoroutineScope()

    Navigator(
        screen = MatcherScreen(),
    ) { navigator ->

        val viewModel = navigator.koinNavigatorScreenModel<AppViewModel>()

        BackHandler(enabled = true) {
            coroutine.launch {
                navigation.emit(
                    Navigation.Event.OnBack
                )
            }
        }

        LaunchedEffect(Unit) {
            navigation.event.collect { event ->
                when (event) {
                    is Navigation.Event.Navigate -> {
                        when (event.screen) {
                            Navigation.Screen.About -> navigator.push(AboutScreen())
                            Navigation.Screen.Libraries -> navigator.push(LibrariesScreen())
                            Navigation.Screen.Matcher -> navigator.popUntilRoot()
                            Navigation.Screen.Validator -> navigator.push(ValidatorScreen())
                        }
                    }

                    Navigation.Event.OnBack -> navigator.pop()
                }

                navigation.update(
                    screen = when (navigator.lastItem) {
                        is MatcherScreen -> Navigation.Screen.Matcher
                        is AboutScreen -> Navigation.Screen.About
                        is LibrariesScreen -> Navigation.Screen.Libraries
                        is ValidatorScreen -> Navigation.Screen.Validator
                        else -> error("Invalid screen")
                    }
                )
            }
        }

        LaunchedEffect(Unit) {
            controller.event.collect { event ->
                when (event) {
                    Command.Clear -> {
                        viewModel.clear()
                        navigator.replace(MatcherScreen())
                        navigation.update(Navigation.Screen.Matcher)
                    }
                }
            }
        }

        FadeTransition(navigator) {
            it.Content()
        }
    }
}
