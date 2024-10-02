/*
 * NeoRegex.
 *
 * Copyright (C) 2024 <AUTHOR>.
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

val LocalFontSizes = compositionLocalOf<FontSizes> { error("FontSizes not defined") }

data class FontSizes(
    val huge: TextUnit = 20.sp,
    val big: TextUnit = 18.sp,
    val medium: TextUnit = 16.sp,
    val default: TextUnit = 14.sp,
    val small: TextUnit = 12.sp,
)