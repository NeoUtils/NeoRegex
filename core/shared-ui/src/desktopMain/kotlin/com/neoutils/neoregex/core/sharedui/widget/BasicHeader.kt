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

@file:OptIn(ExperimentalComposeUiApi::class)

package com.neoutils.neoregex.core.sharedui.widget

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import com.jetbrains.JBR
import com.neoutils.neoregex.core.common.util.DragHandler
import com.neoutils.neoregex.core.common.util.UiMode
import com.neoutils.neoregex.core.common.util.isDark
import com.neoutils.neoregex.core.common.util.resolve
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.sharedui.remember.CompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.WindowFocus
import com.neoutils.neoregex.core.sharedui.remember.rememberCompleteWindowState
import com.neoutils.neoregex.core.sharedui.remember.rememberWindowFocus
import java.awt.Frame
import java.awt.event.MouseEvent
import java.awt.event.WindowEvent

data class BasicHeader(
    val title: String,
    val height: Dp = 40.dp,
    val uiMode: UiMode = UiMode.resolve()
) : WindowWidget {

    @Composable
    override fun FrameWindowScope.Content() {

        val focus = rememberWindowFocus()
        val state = rememberCompleteWindowState()

        val dragHandler = remember { DragHandler(window) }

        val density = LocalDensity.current

        val customTitleBar = remember {
            JBR.windowDecorations?.createCustomTitleBar()?.also {
                it.height = density.run { height.toPx() }
                it.putProperty("controls.dark", uiMode.isDark)
                JBR.windowDecorations?.setCustomTitleBar(window, it)
            }
        }

        Surface(
            color = when (focus) {
                WindowFocus.FOCUSED -> colorScheme.surfaceVariant
                WindowFocus.UNFOCUSED -> colorScheme.surfaceBright
            },
            modifier = customTitleBar?.let {
                Modifier.pointerInput(Unit) {

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

            } ?: Modifier.pointerInput(state) {
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
        ) {
            Box(
                modifier = Modifier
                    .height(height)
                    .padding(6.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = title)

                if (customTitleBar == null) {
                    Buttons(
                        modifier = Modifier.align(
                            Alignment.CenterEnd
                        )
                    )
                }
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

inline fun Modifier.applyIf(
    mustApply: Boolean,
    apply: Modifier. () -> Modifier
): Modifier {
    return if (mustApply) {
        apply()
    } else this
}
