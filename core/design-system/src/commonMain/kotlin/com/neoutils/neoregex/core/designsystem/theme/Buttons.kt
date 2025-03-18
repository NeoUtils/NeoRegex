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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform

data class Buttons(
    val size: Dp,
    val padding: Dp
) {

    companion object {
        val Default
            @Composable
            get() = when (platform) {
                Platform.Android -> Buttons(
                    size = 28.dp,
                    padding = 4.dp
                )

                else -> Buttons(
                    size = 24.dp,
                    padding = 4.dp
                )
            }
    }
}

val LocalButtons = compositionLocalOf<Buttons> { error("Buttons not defined") }