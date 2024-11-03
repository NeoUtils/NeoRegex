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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.neoutils.neoregex.core.common.util.DragHandler
import com.neoutils.neoregex.core.common.util.ColorTheme
import com.neoutils.neoregex.core.common.util.rememberColorTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.remember.CompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.WindowFocus
import com.neoutils.neoregex.core.sharedui.remember.rememberCompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.rememberWindowFocus
import java.awt.Frame
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent

private val DefaultHeaderHeight = 40.dp

@Composable
fun FrameWindowScope.NeoHeader(
    modifier: Modifier = Modifier,
    colorTheme: ColorTheme = rememberColorTheme(),
    content: @Composable BoxScope.(padding: PaddingValues) -> Unit = {},
) {

    val focus = rememberWindowFocus()
    val state = rememberCompleteWindowState()

    val dragHandler = remember { DragHandler(window) }

    // only macOS and Windows supports
    val customTitleBar = remember {
        JBR.windowDecorations?.createCustomTitleBar()
    }

    val density = LocalDensity.current

    LaunchedEffect(window, colorTheme) {
        customTitleBar?.height = density.run { DefaultHeaderHeight.toPx() }
        customTitleBar?.putProperty("controls.dark", colorTheme.isDark)
        JBR.windowDecorations?.setCustomTitleBar(window, customTitleBar)
    }

    Surface(
        color = when (focus) {
            WindowFocus.FOCUSED -> colorScheme.surfaceVariant
            WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
        },
        modifier = modifier.sizeIn(
            maxHeight = DefaultHeaderHeight
        ).onSizeChanged {
            customTitleBar?.height = it.height.toFloat()
        }.run {
            customTitleBar?.let {
                pointerInput(Unit) {

                    var inUserControl = false

                    awaitEachGesture {
                        awaitPointerEvent(PointerEventPass.Main).let { event ->
                            event.changes.forEach {
                                if (!it.isConsumed && !inUserControl) {
                                    customTitleBar.forceHitTest(false)
                                } else {
                                    if (event.type == PointerEventType.Press) {
                                        inUserControl = true
                                    }
                                    if (event.type == PointerEventType.Release) {
                                        inUserControl = false
                                    }
                                    customTitleBar.forceHitTest(true)
                                }
                            }
                        }
                    }
                }
            } ?: pointerInput(state) {
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
                        JBR.windowMove?.startMovingTogetherWithMouse(
                            window,
                            MouseEvent.BUTTON1
                        ) ?: run {
                            dragHandler.onDragStarted()
                        }
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(dimensions.medium)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (customTitleBar == null) {

                val width = remember { mutableStateOf(0.dp) }

                Buttons(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .onSizeChanged {
                            width.value = it.width.dp
                        }
                )

                content(
                    PaddingValues(
                        end = width.value + dimensions.short,
                    )
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
