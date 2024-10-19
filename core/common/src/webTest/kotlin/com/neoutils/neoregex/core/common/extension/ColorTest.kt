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
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorTest {

    @Test
    fun toCss_test() {

        // primary colors
        assertEquals("rgba(255, 0, 0, 1)", Color.Red.toCss())
        assertEquals("rgba(0, 255, 0, 1)", Color.Green.toCss())
        assertEquals("rgba(0, 0, 255, 1)", Color.Blue.toCss())

        // secondary colors
        assertEquals("rgba(255, 0, 255, 1)", Color.Magenta.toCss())
        assertEquals("rgba(0, 255, 255, 1)", Color.Cyan.toCss())
        assertEquals("rgba(255, 255, 0, 1)", Color.Yellow.toCss())

        // other colors
        assertEquals("rgba(0, 0, 0, 0)", Color.Transparent.toCss())
        assertEquals("rgba(0, 0, 0, 1)", Color.Black.toCss())
        assertEquals("rgba(255, 255, 255, 1)", Color.White.toCss())
    }
}
