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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.Controller
import com.neoutils.neoregex.core.sharedui.component.Options
import com.neoutils.neoregex.core.sharedui.extension.surface
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
            topBar = {
                NeoAppBar()
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { padding ->
            App(Modifier.padding(padding))
        }
    }
}

@Composable
private fun NeoAppBar(
    modifier: Modifier = Modifier,
    background: Color = colorScheme.surfaceContainer,
    shadowElevation: Dp = dimensions.tiny,
    height: Dp = 55.dp
) = CenterAlignedTopAppBar(
    navigationIcon = {
        Controller(
            modifier = Modifier
                .padding(dimensions.tiny)
                .height(32.dp)
        )
    },
    title = {
        Text(
            text = stringResource(Res.string.app_name),
            style = typography.titleMedium.copy(
                fontFamily = null,
            ),
        )
    },
    actions = {
        Options(
            modifier = Modifier
                .padding(dimensions.tiny)
                .height(32.dp)
        )
    },
    modifier = modifier.surface(
        shape = RectangleShape,
        backgroundColor = background,
        shadowElevation = LocalDensity.current.run {
            shadowElevation.toPx()
        }
    ),
    expandedHeight = height,
    colors = TopAppBarDefaults.topAppBarColors(
        containerColor = background
    )
)

