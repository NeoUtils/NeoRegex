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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import com.neoutils.neoregex.core.designsystem.theme.NeoBackground
import com.neoutils.neoregex.feature.about.AboutScreen
import com.neoutils.neoregex.feature.matcher.MatcherScreen

@Composable
fun App(
    modifier: Modifier = Modifier
) = NeoBackground(modifier) {
    Navigator(AboutScreen()) { navigator ->
        FadeTransition(navigator) {
            it.Content()
        }
    }
}
