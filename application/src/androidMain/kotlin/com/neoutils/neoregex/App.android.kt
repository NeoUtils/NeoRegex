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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.sharedui.component.Navigation
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import com.neoutils.neoregex.core.sharedui.extension.surface
import org.jetbrains.compose.resources.stringResource

@Composable
fun AndroidApp() = WithKoin {
    NeoTheme {
        Scaffold(
            topBar = {
                NeoAppBar()
            },
            contentWindowInsets = WindowInsets.safeContent
        ) { padding ->
            App(Modifier.padding(padding))
        }
    }
}

@Composable
fun NeoAppBar(
    modifier: Modifier = Modifier,
    background: Color = colorScheme.surfaceContainer,
    shadowElevation: Dp = dimensions.tiny,
    height : Dp =  55.dp
) = TopAppBar(
    title = {
        Box(modifier = Modifier.fillMaxWidth()) {

            Navigation(
                modifier = Modifier
                    .padding(horizontal = dimensions.medium)
                    .align(Alignment.CenterStart)
            )

            Text(
                text = stringResource(Res.string.app_name),
                modifier = Modifier.align(Alignment.Center),
                style = typography.titleMedium.copy(
                    fontFamily = null
                )
            )
        }
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
