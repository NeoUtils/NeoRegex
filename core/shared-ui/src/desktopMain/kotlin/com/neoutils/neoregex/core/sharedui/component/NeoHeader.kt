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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import com.jetbrains.JBR
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.DragHandler
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.TopBarHeight
import com.neoutils.neoregex.core.sharedui.extension.NeoWindowState
import com.neoutils.neoregex.core.sharedui.extension.WindowFocus
import com.neoutils.neoregex.core.sharedui.extension.rememberNeoWindowState
import com.neoutils.neoregex.core.sharedui.extension.rememberWindowFocus
import java.awt.Frame
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent

@Composable
fun FrameWindowScope.NeoHeader(
    modifier: Modifier = Modifier,
    colorTheme: ColorTheme = rememberColorTheme(),
    content: @Composable BoxWithConstraintsScope.(padding: PaddingValues) -> Unit = {},
) {

    val focus = rememberWindowFocus()
    val state = rememberNeoWindowState()

    val dragHandler = remember(window) { DragHandler(window) }

    // only macOS and Windows supports
    val customTitleBar = remember {
        JBR.windowDecorations?.createCustomTitleBar()
    }

    val density = LocalDensity.current

    LaunchedEffect(window, colorTheme) {
        customTitleBar?.height = density.run { TopBarHeight.toPx() }
        customTitleBar?.putProperty("controls.dark", colorTheme.isDark)
        JBR.windowDecorations?.setCustomTitleBar(window, customTitleBar)
    }

    Surface(
        color = when (focus) {
            WindowFocus.FOCUSED -> colorScheme.surfaceVariant
            WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
        },
        modifier = modifier.sizeIn(
            maxHeight = TopBarHeight
        ).onSizeChanged {
            customTitleBar?.height = it.height.toFloat()
        }.run {
            customTitleBar?.let {
                pointerInput(customTitleBar) {
                    awaitEachGesture {
                        awaitPointerEvent(PointerEventPass.Main).let { event ->
                            event.changes.forEach {
                                if (!it.isConsumed) {
                                    customTitleBar.forceHitTest(false)
                                }
                            }
                        }
                    }
                }
            } ?: pointerInput(dragHandler, state) {
                detectTapGestures(
                    onDoubleTap = {
                        when (state) {
                            NeoWindowState.FLOATING -> {
                                window.extendedState = Frame.MAXIMIZED_BOTH
                            }

                            NeoWindowState.MAXIMIZED,
                            NeoWindowState.PINNED -> {
                                window.extendedState = Frame.NORMAL
                            }

                            NeoWindowState.FULLSCREEN,
                            NeoWindowState.MINIMIZED -> error("Invalid")
                        }
                    },
                    onPress = {
                        JBR.windowMove?.startMovingTogetherWithMouse(
                            window,
                            MouseEvent.BUTTON1
                        ) ?: run {
                            // No Runtime of JetBrains
                            dragHandler.onDragStarted()
                        }
                    }
                )
            }
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (customTitleBar == null) {

                var width by remember { mutableStateOf(0.dp) }

                content(
                    PaddingValues(
                        end = width,
                    )
                )

                Buttons(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .onSizeChanged { width = it.width.dp }
                        .padding(end = dimensions.small.s)
                )
            } else {
                content(
                    density.run {
                        PaddingValues(
                            start = customTitleBar.leftInset.toDp(),
                            end = customTitleBar.rightInset.toDp()
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun WindowScope.Buttons(modifier: Modifier) = Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(
        space = dimensions.nano.m,
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
            .size(dimensions.large.m)
            .padding(dimensions.nano.m)
    )
}
