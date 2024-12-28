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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
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
import com.neoutils.neoregex.core.resources.web_warning_text
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import com.neoutils.neoregex.core.sharedui.extension.surface
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
                TopLabel(
                    text = stringResource(Res.string.web_warning_text),
                    visible = preferences.showWebWarning,
                    onClose = {
                        preferencesDataSource.update {
                            it.copy(
                                showWebWarning = false
                            )
                        }
                    }
                ) {
                    Header()
                }
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
    shadowElevation: Dp = dimensions.tiny
) = TopAppBar(
    modifier = modifier.surface(
        shape = RectangleShape,
        backgroundColor = colorScheme.surfaceContainer,
        shadowElevation = LocalDensity.current.run {
            shadowElevation.toPx()
        }
    ),
    title = {
        val screen by navigation.screen.collectAsStateWithLifecycle()

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

@Composable
fun TopLabel(
    text: String,
    visible: Boolean = true,
    onClose: () -> Unit = {},
    content: @Composable () -> Unit
) = Column {

    AnimatedVisibility(visible) {
        ProvideTextStyle(
            typography.labelLarge.copy(
                color = Color.Black
            )
        ) {
            CompositionLocalProvider(
                LocalIndication provides ripple(color = Color.Black)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Yellow)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 8.dp),
                    )

                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .padding(dimensions.tiny)
                            .size(dimensions.large)
                            .clip(CircleShape)
                            .clickable(onClick = onClose)
                            .padding(dimensions.micro)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }
    }

    content()
}

