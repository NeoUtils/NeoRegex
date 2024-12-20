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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.jetbrains.JBR
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.resources.flavicon
import com.neoutils.neoregex.core.sharedui.remember.NeoWindowState
import com.neoutils.neoregex.core.sharedui.remember.rememberNeoWindowState
import com.neoutils.neoregex.core.sharedui.util.NeoRegexWindowExceptionHandlerFactory
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ApplicationScope.NeoWindow(
    icon: Painter = painterResource(Res.drawable.flavicon),
    title: String = stringResource(Res.string.app_name),
    undecorated: Boolean = JBR.windowDecorations == null,
    windowState: WindowState = rememberWindowState(),
    border: BorderStroke = BorderStroke(1.dp, colorScheme.outline),
    exceptionHandlerFactory: WindowExceptionHandlerFactory = NeoRegexWindowExceptionHandlerFactory,
    header: @Composable FrameWindowScope.() -> Unit = {
        NeoHeader {
            Text(
                text = title,
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
    },
    content: @Composable FrameWindowScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalWindowExceptionHandlerFactory
            .provides(exceptionHandlerFactory)
    ) {
        Window(
            icon = icon,
            title = title,
            undecorated = undecorated,
            onCloseRequest = ::exitApplication,
            state = windowState
        ) {

            val completeWindowState = rememberNeoWindowState()

            Surface(
                color = colorScheme.background,
                modifier = when (completeWindowState) {
                    NeoWindowState.FLOATING,
                    NeoWindowState.PINNED -> {
                        if (undecorated) {
                            Modifier.border(border)
                        } else {
                            Modifier
                        }
                    }

                    else -> Modifier
                }
            ) {
                Column {

                    header()

                    HorizontalDivider()

                    content()
                }
            }
        }
    }
}
