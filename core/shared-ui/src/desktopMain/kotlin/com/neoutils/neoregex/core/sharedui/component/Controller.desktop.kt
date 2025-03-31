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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Controller(
    modifier: Modifier,
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(dimensions.tiny),
    verticalAlignment = Alignment.CenterVertically
) {

    val navigation = koinInject<NavigationManager>()

    val coroutine = rememberCoroutineScope()

    Menu()

    Navigation(
        modifier = Modifier.onPointerEvent(PointerEventType.Scroll) {
            val delta = it.changes.first().scrollDelta.y.toInt()

            coroutine.launch {
                if (delta != 0) {
                    when (navigation.screen.value) {
                        Navigation.Screen.Matcher -> {
                            navigation.emit(
                                Navigation.Event.Navigate(
                                    Navigation.Screen.Validator
                                )
                            )
                        }

                        Navigation.Screen.Validator -> {
                            navigation.emit(
                                Navigation.Event.Navigate(
                                    Navigation.Screen.Matcher
                                )
                            )
                        }

                        else -> Unit
                    }
                }
            }
        }
    )
}
