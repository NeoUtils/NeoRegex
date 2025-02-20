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

package com.neoutils.neoregex.core.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme

@Composable
actual fun rememberColorTheme(): ColorTheme {

    return remember {
        when (platform) {
            Platform.Desktop.Linux -> {
                XDGDesktopPortal().use {
                    it.getTheme()
                }
            }

            else -> when (currentSystemTheme) {
                SystemTheme.LIGHT -> ColorTheme.LIGHT_SYSTEM
                SystemTheme.DARK -> ColorTheme.DARK_SYSTEM
                SystemTheme.UNKNOWN -> ColorTheme.LIGHT_SYSTEM
            }
        }
    }
}
