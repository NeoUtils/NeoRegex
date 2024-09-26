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

package com.neo.regex

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.neo.regex.core.common.util.UiMode
import com.neo.regex.core.common.util.isDark
import com.neo.regex.core.common.util.resolve
import com.neo.regex.core.designsystem.theme.NeoTheme
import com.neo.regex.resources.Res
import com.neo.regex.resources.app_name
import com.neo.regex.resources.flavicon
import com.neo.regex.ui.App
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

    Window(
        icon = painterResource(Res.drawable.flavicon),
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        state = rememberWindowState(
            position = WindowPosition.Aligned(
                Alignment.Center
            )
        )
    ) {

        val uiMode = remember { UiMode.resolve() }

        NeoTheme(uiMode.isDark) {
            App()
        }
    }
}