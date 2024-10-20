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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.resources.flavicon
import com.neoutils.neoregex.core.sharedui.widget.BasicHeader
import com.neoutils.neoregex.core.sharedui.widget.WindowWidget
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.NeoWindow(
    icon: Painter = painterResource(Res.drawable.flavicon),
    title: String = stringResource(Res.string.app_name),
    header: WindowWidget? = BasicHeader(title),
    content: @Composable FrameWindowScope.() -> Unit
) {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(
            Alignment.Center
        )
    )

    Window(
        undecorated = true,
        transparent = true,
        icon = icon,
        onCloseRequest = ::exitApplication,
        title = title,
        state = windowState
    ) {
        Surface(
            color = colorScheme.background,
            modifier = when (windowState.placement) {
                WindowPlacement.Floating -> {
                    Modifier.border(
                        width = 1.dp,
                        color = colorScheme.outline,
                        shape = RectangleShape,
                    )
                }

                WindowPlacement.Maximized,
                WindowPlacement.Fullscreen -> {
                    Modifier
                }
            }
        ) {
            Column {

                // TODO: improve with context parameters
                header?.run { Content() }

                HorizontalDivider()

                content()
            }
        }
    }
}
