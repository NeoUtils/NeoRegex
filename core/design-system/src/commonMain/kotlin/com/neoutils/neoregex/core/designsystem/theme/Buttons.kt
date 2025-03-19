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

package com.neoutils.neoregex.core.designsystem.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.buttons

data class Buttons(
    val default: Config,
    val small: Config
) {
    data class Config(
        val size: Dp,
        val padding: Dp
    )

    companion object {
        val Default
            @Composable
            get() = when (platform) {
                Platform.Android -> Buttons(
                    default = Config(
                        size = 28.dp,
                        padding = 4.dp
                    ),
                    small = Config(
                        size = 20.dp,
                        padding = 2.dp
                    )
                )

                else -> Buttons(
                    default = Config(
                        size = 24.dp,
                        padding = 4.dp
                    ),
                    small = Config(
                        size = 18.dp,
                        padding = 2.dp
                    )
                )
            }
    }
}

val LocalButtons = compositionLocalOf<Buttons> { error("Buttons not defined") }

@Composable
fun Modifier.configButton(
    config: Buttons.Config = buttons.default
) = size(config.size)
    .padding(config.padding)