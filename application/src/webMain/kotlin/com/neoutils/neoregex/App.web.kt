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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.neoutils.neoregex

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.extension.toCss
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.component.LinkColor
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.NavigationManager
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun WebApp() = WithKoin {

    val preferencesDataSource = koinInject<PreferencesDataSource>()

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    NeoTheme(
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) {

        val background = colorScheme.background.toCss()

        LaunchedEffect(background) {
            val body = checkNotNull(document.body)
            body.style.backgroundColor = background
        }

        Scaffold(
            topBar = {
                Header()
            },
        ) {
            App(Modifier.padding(it))
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
) {
    val screen by navigation.screen.collectAsStateWithLifecycle()

    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.app_name),
                    style = typography.titleMedium.copy(
                        fontFamily = null,
                    ),
                )

                Spacer(Modifier.width(18.dp))

                val coroutine = rememberCoroutineScope()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val colors = LinkColor(
                        idle = colorScheme.onSurface,
                        hover = colorScheme.onSurface.copy(alpha = 0.8f),
                        press = colorScheme.onSurface.copy(alpha = 0.6f),
                        pressed = colorScheme.onSurface
                    )

                    Link(
                        text = "Matcher",
                        onClick = {
                            coroutine.launch {
                                navigation.navigate(
                                    Navigation.Event.Matcher
                                )
                            }
                        },
                        style = typography.labelMedium.copy(
                            textDecoration = TextDecoration.None,
                            fontWeight = if (screen == Navigation.Screen.Matcher) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        ),
                        enabled = screen != Navigation.Screen.Matcher,
                        colors = colors,
                    )

                    Link(
                        text = "About",
                        onClick = {
                            coroutine.launch {
                                navigation.navigate(
                                    Navigation.Event.About
                                )
                            }
                        },
                        style = typography.labelMedium.copy(
                            textDecoration = TextDecoration.None,
                            fontWeight = if (screen == Navigation.Screen.About) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        ),
                        enabled = screen != Navigation.Screen.About,
                        colors = colors
                    )

                    AnimatedVisibility(
                        visible = screen == Navigation.Screen.Libraries
                    ) {
                        Link(
                            text = "Libraries",
                            style = typography.labelMedium.copy(
                                textDecoration = TextDecoration.None,
                                fontWeight = FontWeight.Bold
                            ),
                            enabled = false,
                            colors = colors
                        )
                    }
                }
            }
        },
        actions = {
            Options(
                modifier = Modifier
                    .padding(dimensions.short)
                    .height(32.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    dimensions.short,
                    Alignment.End
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.surfaceContainer,
            titleContentColor = colorScheme.onSurface
        )
    )
}

@Composable
fun Experimental(
    size: DpSize = DpSize(300.dp, 300.dp),
    content: @Composable () -> Unit
) = Box {

    content()

    val density = LocalDensity.current

    val translation = density.run {
        Offset(
            x = (size.width / 4.5f).toPx(),
            y = (size.height / 4.5f).toPx().unaryMinus()
        )
    }

    Box(
        modifier = Modifier
            .size(size)
            .align(Alignment.TopEnd)
            .graphicsLayer(
                translationX = translation.x,
                translationY = translation.y,
                rotationZ = 45f
            )
    ) {
        Text(
            text = "experimental",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(Color.Yellow)
                .padding(vertical = 8.dp),
        )
    }
}

