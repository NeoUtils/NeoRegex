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

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.NavigationManager
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun CompactControl(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    textStyle: TextStyle = TextStyle()
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(dimensions.tiny),
    verticalAlignment = Alignment.CenterVertically
) {
    Menu(
        navigation = navigation,
        modifier = Modifier.fillMaxHeight()
    )

    Navigation(
        navigation = navigation,
        modifier = Modifier.fillMaxHeight(),
        textStyle = textStyle
    )
}

@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center
) {

    val canPopBack by navigation.canPopBack.collectAsStateWithLifecycle()

    val coroutine = rememberCoroutineScope()

    val expanded = remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = canPopBack,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { showBackButton ->
        if (showBackButton) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            coroutine.launch {
                                navigation.emit(Navigation.Event.OnBack)
                            }
                        }
                    )
                    .padding(dimensions.medium)
                    .aspectRatio(ratio = 1f)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { expanded.value = true }
                    .padding(dimensions.tiny)
                    .padding(1.dp)
                    .aspectRatio(ratio = 1f)
            )
        }
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false },
        modifier = Modifier.padding(top = dimensions.tiny)
    ) {
        DropdownMenuItem(
            text = { Text(text = "New") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            onClick = {
                // TODO: implement
                expanded.value = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = "About") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.About
                        )
                    )
                    expanded.value = false
                }
            }
        )
    }
}

@Composable
private fun Navigation(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    textStyle: TextStyle = TextStyle()
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center
) {
    val current by navigation.screen.collectAsStateWithLifecycle()

    val expanded = remember { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null
    ).merge(textStyle)

    val canSelectScreen = when (current) {
        Navigation.Screen.About -> false
        Navigation.Screen.Libraries -> false
        Navigation.Screen.Matcher -> true
        Navigation.Screen.Validator -> true
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(dimensions.tiny))
            .clickable(canSelectScreen) { expanded.value = true }
            .padding(dimensions.tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(current.title),
            style = mergedTextStyle
        )

        AnimatedVisibility(
            visible = canSelectScreen,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally(),
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                modifier = Modifier.size(18.dp),
                contentDescription = null
            )
        }
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(Navigation.Screen.Matcher.title),
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Matcher
                        )
                    )
                    expanded.value = false
                }
            },
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(Navigation.Screen.Validator.title),
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Validator
                        )
                    )
                    expanded.value = false
                }
            },
        )
    }
}