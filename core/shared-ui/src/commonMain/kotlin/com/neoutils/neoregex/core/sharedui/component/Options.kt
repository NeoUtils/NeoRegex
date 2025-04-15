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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun Options(
    modifier: Modifier = Modifier,
    preferencesDataSource: PreferencesDataSource = koinInject(),
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(
        dimensions.small.s,
        Alignment.End
    ),
    verticalAlignment = Alignment.CenterVertically
) {

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    val uriHandler = LocalUriHandler.current

    Icon(
        painter = painterResource(Res.drawable.github),
        contentDescription = null,
        tint = colorScheme.onSurface,
        modifier = Modifier
            .clip(CircleShape)
            .aspectRatio(ratio = 1f)
            .clickable(
                onClick = {
                    uriHandler.openUri(
                        uri = "https://github.com/NeoUtils/NeoRegex"
                    )
                }
            ).padding(dimensions.nano.m)
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
            .aspectRatio(ratio = 1f)
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
            ).padding(dimensions.nano.m)
    )
}