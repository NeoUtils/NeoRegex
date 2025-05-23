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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.component.LinkColor
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_title
import com.neoutils.neoregex.core.resources.screen_libraries
import kotlinx.browser.window
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
actual fun Controller(
    modifier: Modifier,
) = Row(
    modifier = modifier.horizontalScroll(rememberScrollState()),
    horizontalArrangement = Arrangement.spacedBy(dimensions.small.s),
    verticalAlignment = Alignment.CenterVertically
) {
    val coroutine = rememberCoroutineScope()

    val navigation = koinInject<NavigationManager>()

    val screen by navigation.screen.collectAsStateWithLifecycle()

    val textStyle = typography.labelLarge.copy(fontFamily = null)

    LaunchedEffect(Unit) {
        navigation.screen.collectLatest {
            window.document.title = getString(
                Res.string.app_title,
                getString(screen.title)
            )
        }
    }

    listOf(
        Navigation.Screen.Matcher,
        Navigation.Screen.Validator,
        Navigation.Screen.About,
    ).forEach {
        NavigateButton(
            text = stringResource(it.title),
            selected = screen == it,
            onNavigate = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = it
                        )
                    )
                }
            },
            textStyle = textStyle
        )
    }

    AnimatedVisibility(
        visible = screen == Navigation.Screen.Libraries
    ) {
        Link(
            text = stringResource(Res.string.screen_libraries),
            style = textStyle.copy(
                textDecoration = TextDecoration.None,
                fontWeight = FontWeight.Bold
            ),
            colors = LinkColor(
                idle = colorScheme.onSurface,
                hover = colorScheme.onSurface.copy(alpha = 0.8f),
                press = colorScheme.onSurface.copy(alpha = 0.6f),
                pressed = colorScheme.onSurface
            ),
            enabled = false
        )
    }
}

@Composable
private fun NavigateButton(
    text: String,
    selected: Boolean,
    onNavigate: () -> Unit,
    textStyle: TextStyle
) {
    val colors = LinkColor(
        idle = colorScheme.onSurface,
        hover = colorScheme.onSurface.copy(alpha = 0.8f),
        press = colorScheme.onSurface.copy(alpha = 0.6f),
        pressed = colorScheme.onSurface
    )

    Link(
        text = text,
        onClick = onNavigate,
        style = textStyle.copy(
            textDecoration = TextDecoration.None,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        ),
        colors = colors,
    )
}
