/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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

package com.neoutils.neoregex.core.common.manager

import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.common.model.TextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TextHistoryManager {

    private var undoStack: Entry? = null
    private var redoStack: Entry? = null

    private val _flow = MutableStateFlow(HistoryState())
    val flow = _flow.asStateFlow()

    fun push(value: TextState) {

        if (shouldPush(value)) {

            undoStack = Entry(value, undoStack)
            redoStack = null

            update()
            return
        }

        if (shouldUpdateLast(value)) {

            undoStack?.value = value

            update()
        }
    }

    private fun shouldPush(value: TextState): Boolean {
        return undoStack?.value?.value != value.value
    }

    private fun shouldUpdateLast(value: TextState): Boolean {
        return redoStack == null && value != undoStack?.value
    }

    fun undo(): TextState? {
        val entry = undoStack ?: return null

        undoStack = entry.next ?: return null
        redoStack = Entry(entry.value, redoStack)

        update()

        return undoStack?.value
    }

    fun redo(): TextState? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        update()

        return entry.value
    }

    private fun update() {
        _flow.value = HistoryState(
            canUndo = undoStack?.next != null,
            canRedo = redoStack != null,
        )
    }

    fun clear() {
        redoStack = null
        undoStack = null

        update()
    }

    data class Entry(
        var value: TextState,
        val next: Entry? = null
    )
}
