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

@file:OptIn(ExperimentalComposeUiApi::class)

package com.neoutils.neoregex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.extension.observe
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.datasource.remember.rememberWindowState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.*
import com.neoutils.neoregex.core.sharedui.component.NeoHeader
import com.neoutils.neoregex.core.sharedui.component.NeoWindow
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ApplicationScope.DesktopApp() = WithKoin {
    val preferencesDataSource = koinInject<PreferencesDataSource>()
    val preferences by preferencesDataSource.flow.collectAsState()

    val windowStateDataSource = koinInject<WindowStateDataSource>()
    val windowState by windowStateDataSource.flow.collectAsState()

    NeoTheme(
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) {
        NeoWindow(
            header = { HeaderImpl() },
            windowState = rememberWindowState(windowState)
        ) {

            windowStateDataSource.observe(window)

            App()
        }
    }
}

@Composable
private fun FrameWindowScope.HeaderImpl() = NeoHeader { padding ->

    val preferencesDataSource = koinInject<PreferencesDataSource>()
    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    Text(
        text = stringResource(Res.string.app_name),
        modifier = Modifier.align(
            Alignment.Center
        )
    )

    Row(
        modifier = Modifier
            .padding(padding)
            .padding(horizontal = dimensions.medium)
            .align(Alignment.CenterEnd),
        horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
    ) {
        val uriHandler = LocalUriHandler.current

        Icon(
            painter = painterResource(Res.drawable.github),
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        uriHandler.openUri(
                            uri = "https://github.com/NeoUtils/NeoRegex"
                        )
                    }
                )
                .padding(dimensions.medium)
                .aspectRatio(ratio = 1f)
        )

        Icon(
            painter = when (preferences.colorTheme) {
                Preferences.ColorTheme.SYSTEM -> painterResource(Res.drawable.contrast)
                Preferences.ColorTheme.LIGHT -> painterResource(Res.drawable.light_theme)
                Preferences.ColorTheme.DARK -> painterResource(Res.drawable.dark_theme)
            },
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        preferencesDataSource.update {
                            it.copy(
                                colorTheme = when (it.colorTheme) {
                                    Preferences.ColorTheme.SYSTEM -> Preferences.ColorTheme.LIGHT
                                    Preferences.ColorTheme.LIGHT -> Preferences.ColorTheme.DARK
                                    Preferences.ColorTheme.DARK -> Preferences.ColorTheme.SYSTEM
                                }
                            )
                        }
                    }
                )
                .padding(dimensions.medium)
                .aspectRatio(ratio = 1f)
        )
    }
}
