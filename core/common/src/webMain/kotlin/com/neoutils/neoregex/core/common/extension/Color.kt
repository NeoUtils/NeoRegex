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

package com.neoutils.neoregex.core.common.extension

import androidx.compose.ui.graphics.Color

fun Color.toRgbaString(): String {

    val red = (red * 255).toInt()
    val green = (green * 255).toInt()
    val blue = (blue * 255).toInt()

    return "rgba($red, $green, $blue, $alpha)"
}

@OptIn(ExperimentalStdlibApi::class)
fun Color.toHexString() = value.toHexString(
    format = HexFormat {
        number {
            upperCase = true
            prefix = "#"
        }
    },

).substring(0, 9)
