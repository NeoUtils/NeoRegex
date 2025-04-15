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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.neoutils.neoregex.core.common.util.Variants

val LocalFontSizes = compositionLocalOf<FontSizes> { error("FontSizes not defined") }

data class FontSizes(
    val small: Variants<TextUnit> = Variants(
        s = 10.sp,
        m = 12.sp,
        x = 14.sp
    ),
    val medium: Variants<TextUnit> = Variants(
        s = 16.sp,
        m = 18.sp,
        x = 20.sp
    ),
    val large: Variants<TextUnit> = Variants(
        s = 22.sp,
        m = 24.sp,
        x = 26.sp
    )
)