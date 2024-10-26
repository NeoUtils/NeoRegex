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

package com.neoutils.neoregex.core.sharedui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import com.jetbrains.JBR
import com.neoutils.neoregex.core.common.platform.DesktopOS
import com.neoutils.neoregex.core.common.util.DragHandler
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.remember.CompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.WindowFocus
import com.neoutils.neoregex.core.sharedui.remember.rememberCompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.rememberWindowFocus
import java.awt.Frame
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent

data class BasicHeader(
    val title: String
) : WindowWidget {

    @Composable
    override fun FrameWindowScope.Content() {

        val focus = rememberWindowFocus()
        val state = rememberCompleteWindowState()

        val dragHandler = remember { DragHandler(window) }

        Surface(
            color = when (focus) {
                WindowFocus.FOCUSED -> colorScheme.surfaceVariant
                WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
            },
            modifier = Modifier.pointerInput(state) {
                detectTapGestures(
                    onDoubleTap = {
                        when (state) {
                            CompleteWindowState.FLOATING -> {
                                window.extendedState = Frame.MAXIMIZED_BOTH
                            }

                            CompleteWindowState.MAXIMIZED,
                            CompleteWindowState.PINNED -> {
                                window.extendedState = Frame.NORMAL
                            }

                            CompleteWindowState.FULLSCREEN,
                            CompleteWindowState.MINIMIZED -> error("Invalid")
                        }
                    },
                    onPress = {
                        when (DesktopOS.Current) {
                            DesktopOS.WINDOWS, DesktopOS.MAC_OS -> {
                                dragHandler.onDragStarted()
                            }

                            DesktopOS.LINUX -> {
                                JBR.windowMove?.startMovingTogetherWithMouse(
                                    window,
                                    MouseEvent.BUTTON1
                                ) ?: run {
                                    dragHandler.onDragStarted()
                                }
                            }
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .padding(6.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = title)

                Buttons(
                    modifier = Modifier.align(
                        Alignment.CenterEnd
                    )
                )
            }
        }
    }

    @Composable
    fun WindowScope.Buttons(modifier: Modifier) = Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = dimensions.tiny,
            alignment = Alignment.CenterHorizontally
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = {
                        window.dispatchEvent(
                            WindowEvent(
                                window,
                                WindowEvent.WINDOW_CLOSING
                            )
                        )
                    }
                )
                .padding(dimensions.medium)
                .aspectRatio(ratio = 1f)
        )
    }
}
