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

package com.neo.regex.feature.matcher.manager

import com.neo.regex.feature.matcher.model.TextState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class HistoryManager {

    private var undoStack: Entry? = null
    private var redoStack: Entry? = null

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var lock = AtomicBoolean(false)

    fun push(value: TextState) {

        if (lock.get()) return

        if (shouldPush(value)) {

            undoStack = Entry(value, undoStack)
            redoStack = null

            updateState()
            return
        }

        if (shouldUpdateLast(value)) {

            undoStack?.value = value

            updateState()
        }
    }

    private fun shouldPush(value: TextState): Boolean {
        return undoStack?.value?.text != value.text
    }

    private fun shouldUpdateLast(value: TextState): Boolean {
        return redoStack == null && value != undoStack?.value
    }

    fun undo(): TextState? {
        val entry = undoStack ?: return null

        undoStack = entry.next ?: return null
        redoStack = Entry(entry.value, redoStack)

        updateState()

        lock.set(true)

        return undoStack?.value
    }

    fun redo(): TextState? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        updateState()

        lock.set(true)

        return entry.value
    }

    fun unlock() {
        lock.set(false)
    }

    private fun updateState() {
        _state.value = State(
            canUndo = undoStack?.next != null,
            canRedo = redoStack != null,
        )
    }

    data class Entry(
        var value: TextState,
        val next: Entry? = null
    )

    data class State(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
    )
}
