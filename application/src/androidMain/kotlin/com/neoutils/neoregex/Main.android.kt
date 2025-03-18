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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.flowWithLifecycle
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.colorTheme
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferencesDataSource: PreferencesDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSystemBars()

        setContent {
            AndroidApp()
        }
    }

    private fun setupSystemBars() = lifecycle.coroutineScope.launch {
        preferencesDataSource.flow.flowWithLifecycle(
            lifecycle = lifecycle
        ).collect { preferences ->

            val colorTheme = when (preferences.colorTheme) {
                Preferences.ColorTheme.SYSTEM -> colorTheme
                Preferences.ColorTheme.LIGHT -> ColorTheme.LIGHT
                Preferences.ColorTheme.DARK -> ColorTheme.DARK
            }

            val style = when (colorTheme) {
                ColorTheme.DARK, ColorTheme.DARK_SYSTEM -> {
                    SystemBarStyle.dark(
                        Color.Transparent.toArgb(),
                    )
                }

                ColorTheme.LIGHT, ColorTheme.LIGHT_SYSTEM -> {
                    SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb(),
                    )
                }
            }

            enableEdgeToEdge(style, style)
        }
    }
}
