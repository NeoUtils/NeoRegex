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

package com.neoutils.neoregex

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.colorTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSystemBars()

        setContent {
            AndroidApp()
        }
    }

    private fun setupSystemBars() {

        val style = when (colorTheme) {
            ColorTheme.DARK, ColorTheme.DARK_SYSTEM -> {
                SystemBarStyle.dark(
                    Color.BLACK,
                )
            }

            ColorTheme.LIGHT, ColorTheme.LIGHT_SYSTEM -> {
                SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK,
                )
            }
        }

        enableEdgeToEdge(style, style)
    }
}
