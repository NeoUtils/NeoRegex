/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu S. Silva.
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

package com.neoutils.neoregex.core.common.util

import androidx.compose.ui.unit.IntOffset
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Window
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter

class DragHandler(
    private val window: Window
) {
    private var windowLocationAtDragStart: IntOffset? = null
    private var dragStartPoint: IntOffset? = null

    private val currentPointerLocation
        get() = MouseInfo.getPointerInfo()?.location?.toOffset()

    private val dragListener = object : MouseMotionAdapter() {
        override fun mouseDragged(event: MouseEvent) = onDrag()
    }

    private val removeListener = object : MouseAdapter() {
        override fun mouseReleased(event: MouseEvent) {
            window.removeMouseMotionListener(dragListener)
            window.removeMouseListener(this)
        }
    }

    fun onDragStarted() {
        dragStartPoint = currentPointerLocation ?: return
        windowLocationAtDragStart = window.location.toOffset()

        window.addMouseListener(removeListener)
        window.addMouseMotionListener(dragListener)
    }

    private fun onDrag() {
        val windowLocationAtDragStart = windowLocationAtDragStart ?: return
        val dragStartPoint = dragStartPoint ?: return
        val point = currentPointerLocation ?: return

        val newLocation = windowLocationAtDragStart + point - dragStartPoint

        window.setLocation(newLocation.x, newLocation.y)
    }

    private fun Point.toOffset() = IntOffset(x = x, y = y)
}
