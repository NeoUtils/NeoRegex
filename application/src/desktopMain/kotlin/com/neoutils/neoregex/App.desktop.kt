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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.datasource.extension.observe
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.datasource.remember.rememberWindowState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.dispatcher.di.dispatcherModule
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.Controller
import com.neoutils.neoregex.core.sharedui.component.NeoHeader
import com.neoutils.neoregex.core.sharedui.component.NeoWindow
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import com.neoutils.neoregex.di.appModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import com.neoutils.neoregex.feature.validator.di.validatorModule
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun ApplicationScope.DesktopApp() = WithKoin(
    dataSourceModule,
    repositoryModule,
    dispatcherModule,
    matcherModule,
    validatorModule,
    appModule
) {

    val preferencesDataSource = koinInject<PreferencesDataSource>()
    val windowStateDataSource = koinInject<WindowStateDataSource>()

    val preferences by preferencesDataSource.flow.collectAsState()
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
private fun FrameWindowScope.HeaderImpl(
    modifier: Modifier = Modifier,
    preferencesDataSource: PreferencesDataSource = koinInject()
) {

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    NeoHeader(
        modifier = modifier,
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) { padding ->

        Controller(
            modifier = Modifier
                .padding(padding)
                .align(Alignment.CenterStart)
        )

        Text(
            text = stringResource(Res.string.app_name),
            style = typography.titleSmall.copy(
                fontFamily = null
            ),
            modifier = Modifier.align(
                Alignment.Center
            )
        )

        Options(
            modifier = Modifier
                .padding(padding)
                .align(Alignment.CenterEnd)
        )
    }
}
