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

import com.neoutils.neoregex.core.common.util.UiMode.DARK
import com.neoutils.neoregex.core.common.util.UiMode.LIGHT
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.SystemTheme
import org.jetbrains.skiko.currentSystemTheme
import org.jetbrains.skiko.hostOs

fun UiMode.Companion.resolve(): UiMode {

    return when (hostOs) {
        OS.Linux -> {
            XDGDesktopPortal().use {
                it.getTheme()
            }
        }

        else -> when (currentSystemTheme) {
            SystemTheme.LIGHT -> LIGHT
            SystemTheme.DARK -> DARK
            SystemTheme.UNKNOWN -> LIGHT
        }
    }
}
