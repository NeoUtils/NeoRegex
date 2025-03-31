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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.Controller
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.component.SalvageAction
import com.neoutils.neoregex.core.sharedui.component.SalvageUi
import com.neoutils.neoregex.core.sharedui.extension.surface
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun AndroidApp() {

    val preferencesDataSource = koinInject<PreferencesDataSource>()

    val preferences by preferencesDataSource.flow.collectAsState()

    NeoTheme(
        colorTheme = when (preferences.colorTheme) {
            Preferences.ColorTheme.SYSTEM -> rememberColorTheme()
            Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
            Preferences.ColorTheme.DARK -> ColorTheme.DARK
        }
    ) {
        Scaffold(
            topBar = { NeoAppBar() },
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { padding ->
            App(Modifier.padding(padding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NeoAppBar(
    modifier: Modifier = Modifier,
    salvageManager: SalvageManager = koinInject(),
    shadowElevation: Dp = dimensions.tiny,
    height: Dp = 55.dp
) = CenterAlignedTopAppBar(
    navigationIcon = {
        Controller(
            modifier = Modifier.padding(
                start = dimensions.tiny
            )
        )
    },
    title = {

        val coroutine = rememberCoroutineScope()

        AnimatedContent(
            targetState = salvageManager
                .flow
                .collectAsStateWithLifecycle(
                    initialValue = null
                ).value,
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
                    style = typography.titleMedium.copy(
                        fontFamily = null,
                    ),
                )
            } else {
                SalvageUi(
                    opened = salvage,
                    onAction = { action ->
                        when (action) {
                            SalvageAction.Close -> {
                                coroutine.launch {
                                    salvageManager.close()
                                    //navigation.emit(Navigation.Event.Invalidate())
                                }
                            }

                            SalvageAction.Update -> {
                                coroutine.launch {
                                    salvageManager.update()
                                }
                            }

                            is SalvageAction.ChangeName -> {
                                coroutine.launch {
                                    salvageManager.update {
                                        it.copy(
                                            title = action.name
                                        )
                                    }
                                }
                            }

                            SalvageAction.Reset -> {
                                coroutine.launch {
                                    salvageManager.sync()
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
                end = dimensions.small
            )
        )
    },
    modifier = modifier.surface(
        shape = RectangleShape,
        backgroundColor = colorScheme.surfaceVariant,
        shadowElevation = LocalDensity.current.run {
            shadowElevation.toPx()
        }
    ),
    expandedHeight = height,
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = colorScheme.surfaceVariant
    )
)

