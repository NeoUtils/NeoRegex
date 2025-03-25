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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.database.di.databaseModule
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.datasource.extension.observe
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.datasource.remember.rememberWindowState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.di.dispatcherModule
import com.neoutils.neoregex.core.manager.di.managerModule
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.*
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import com.neoutils.neoregex.core.sharedui.remember.WindowFocus
import com.neoutils.neoregex.core.sharedui.remember.rememberWindowFocus
import com.neoutils.neoregex.di.appModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import com.neoutils.neoregex.feature.validator.di.validatorModule
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.DesktopApp() = WithKoin(
    managerModule,
    dataSourceModule,
    databaseModule,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FrameWindowScope.HeaderImpl(
    modifier: Modifier = Modifier,
    preferencesDataSource: PreferencesDataSource = koinInject(),
    salvageManager: SalvageManager = koinInject()
) {

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()
    val salvage by salvageManager.salvage.collectAsStateWithLifecycle(initialValue = null)

    NeoHeader(
        modifier = modifier,
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) { padding ->

        val direction = LocalLayoutDirection.current

        val focus = rememberWindowFocus()

        val coroutine = rememberCoroutineScope()

        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = when (focus) {
                    WindowFocus.FOCUSED -> colorScheme.surfaceVariant
                    WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
                },
            ),
            navigationIcon = {
                Controller(
                    modifier = Modifier.padding(
                        start = padding.calculateStartPadding(direction) + dimensions.tiny
                    )
                )
            },
            title = {
                AnimatedContent(
                    targetState = salvage,
                    contentKey = { it != null },
                    transitionSpec = {
                        slideIntoContainer(
                            SlideDirection.Down
                        ) togetherWith slideOutOfContainer(
                            SlideDirection.Down
                        )
                    },
                    contentAlignment = Alignment.Center,
                ) { salvage ->
                    if (salvage == null) {
                        Text(
                            text = stringResource(Res.string.app_name),
                            style = typography.titleSmall.copy(
                                fontFamily = null
                            )
                        )
                    } else {
                        SalvageUi(
                            salvage = salvage,
                            onAction = {
                                when (it) {
                                    SalvageAction.Close -> {
                                        salvageManager.close()
                                    }
                                    SalvageAction.Update -> {
                                        coroutine.launch {
                                            salvageManager.update()
                                        }
                                    }

                                    is SalvageAction.ChangeName -> {
                                        coroutine.launch {
                                            salvageManager.changeName(it.name)
                                        }
                                    }

                                    SalvageAction.Reset -> {
                                        coroutine.launch {
                                            salvageManager.reset()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            },
            actions = {
                Options(
                    modifier = Modifier.padding(
                        end = padding.calculateEndPadding(direction) + dimensions.tiny
                    )
                )
            }
        )
    }
}

