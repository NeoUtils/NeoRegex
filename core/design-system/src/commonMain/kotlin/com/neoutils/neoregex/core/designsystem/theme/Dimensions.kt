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

package com.neoutils.neoregex.core.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalDimensions = compositionLocalOf<Dimensions> { error("Dimensions not defined") }

data class Dimensions(
    val nano: Variants = Variants(
        s = 2.dp,
        m = 4.dp,
        x = 6.dp
    ),
    val small: Variants = Variants(
        s = 8.dp,
        m = 10.dp,
        x = 12.dp
    ),
    val default: Variants = Variants(
        s = 14.dp,
        m = 16.dp,
        x = 18.dp
    ),
    val large: Variants = Variants(
        s = 20.dp,
        m = 24.dp,
        x = 28.dp
    ),
    val huge: Variants = Variants(
        s = 32.dp,
        m = 40.dp,
        x = Dp.Unspecified
    )
)

data class Variants(
    val s: Dp,
    val m: Dp,
    val x: Dp
)