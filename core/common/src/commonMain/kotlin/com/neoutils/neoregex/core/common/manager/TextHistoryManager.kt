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

import com.neoutils.neoregex.core.common.model.History
import com.neoutils.neoregex.core.common.model.Text
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TextHistoryManager {

    private var undoStack: Entry? = null
    private var redoStack: Entry? = null

    private val _flow = MutableStateFlow(History())
    val flow = _flow.asStateFlow()

    fun push(value: Text) {

        if (shouldPush(value)) {

            undoStack = Entry(value.copy(registered = true), undoStack)
            redoStack = null

            update()
            return
        }

        if (shouldUpdateLast(value)) {

            undoStack?.value = value.copy(registered = true)

            update()
        }
    }

    private fun shouldPush(value: Text): Boolean {
        return undoStack?.value?.text != value.text
    }

    private fun shouldUpdateLast(value: Text): Boolean {
        return redoStack == null && value != undoStack?.value
    }

    fun undo(): Text? {
        val entry = undoStack ?: return null

        undoStack = entry.next ?: return null
        redoStack = Entry(entry.value, redoStack)

        update()

        return undoStack?.value
    }

    fun redo(): Text? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        update()

        return entry.value
    }

    private fun update() {
        _flow.value = History(
            canUndo = undoStack?.next != null,
            canRedo = redoStack != null,
        )
    }

    data class Entry(
        var value: Text,
        val next: Entry? = null
    )
}
