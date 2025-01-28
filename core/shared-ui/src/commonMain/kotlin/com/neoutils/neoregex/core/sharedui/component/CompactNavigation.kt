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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
fun CompactNavigation(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    textStyle: TextStyle = TextStyle()
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(dimensions.tiny),
    verticalAlignment = Alignment.CenterVertically
) {
    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null
    ).merge(textStyle)

    val current by navigation.screen.collectAsStateWithLifecycle()

    val canPop by navigation.canPopBack.collectAsStateWithLifecycle()

    val coroutines = rememberCoroutineScope()

    AnimatedVisibility(canPop) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        coroutines.launch {
                            navigation.emit(Navigation.Event.OnBack)
                        }
                    }
                )
                .padding(dimensions.medium)
                .aspectRatio(ratio = 1f)
        )
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.Center
    ) {
        val expanded = remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(dimensions.tiny))
                .clickable { expanded.value = true }
                .padding(dimensions.tiny),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = stringResource(current.title),
                style = mergedTextStyle
            )

            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                modifier = Modifier.size(18.dp),
                contentDescription = null
            )
        }

        val coroutine = rememberCoroutineScope()

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

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(Navigation.Screen.About.title),
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
                },
            )
        }
    }
}