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
    fun toRgbaString_test() {

        // primary colors
        assertEquals("rgba(255, 0, 0, 1)", Color.Red.toRgbaString())
        assertEquals("rgba(0, 255, 0, 1)", Color.Green.toRgbaString())
        assertEquals("rgba(0, 0, 255, 1)", Color.Blue.toRgbaString())

        // secondary colors
        assertEquals("rgba(255, 0, 255, 1)", Color.Magenta.toRgbaString())
        assertEquals("rgba(0, 255, 255, 1)", Color.Cyan.toRgbaString())
        assertEquals("rgba(255, 255, 0, 1)", Color.Yellow.toRgbaString())

        // other colors
        assertEquals("rgba(0, 0, 0, 0)", Color.Transparent.toRgbaString())
        assertEquals("rgba(0, 0, 0, 1)", Color.Black.toRgbaString())
        assertEquals("rgba(255, 255, 255, 1)", Color.White.toRgbaString())
    }

    @Test
    fun toHexString_test() {

        // primary colors
        assertEquals("#FFFF0000", Color.Red.toHexString())
        assertEquals("#FF00FF00", Color.Green.toHexString())
        assertEquals("#FF0000FF", Color.Blue.toHexString())

        // secondary colors
        assertEquals("#FFFF00FF", Color.Magenta.toHexString())
        assertEquals("#FF00FFFF", Color.Cyan.toHexString())
        assertEquals("#FFFFFF00", Color.Yellow.toHexString())

        // other colors
        assertEquals("#00000000", Color.Transparent.toHexString())
        assertEquals("#FF000000", Color.Black.toHexString())
        assertEquals("#FFFFFFFF", Color.White.toHexString())
    }
}
