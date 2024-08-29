package com.neo.regex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistoryManager<T>(
    private val initialValue: T
) {

    private var undoStack: Entry? = null
    private var redoStack: Entry? = null

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private inner class Entry(
        val value: T,
        val next: Entry? = null
    )

    fun snapshot(value: T) {
        undoStack = Entry(value, next = undoStack ?: Entry(initialValue))
        redoStack = null

        _state.update {
            it.copy(
                canUndo = true,
                canRedo = false
            )
        }
    }

    fun undo(): T? {
        val entry = undoStack ?: return null

        undoStack = entry.next
        redoStack = Entry(entry.value, redoStack)

        _state.update {
            it.copy(
                canUndo = undoStack != null,
                canRedo = true
            )
        }

        return entry.value
    }

    fun redo(): T? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        _state.update {
            it.copy(
                canUndo = true,
                canRedo = redoStack != null
            )
        }

        return entry.value
    }

    data class State(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )
}
