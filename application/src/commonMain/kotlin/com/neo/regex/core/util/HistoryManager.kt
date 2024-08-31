package com.neo.regex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HistoryManager<T> {

    private var undoStack: Entry<T>? = null
    private var redoStack: Entry<T>? = null

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    fun snapshot(value: T) {
        undoStack = Entry(value, undoStack)
        redoStack = null

        updateState()
    }

    fun undo(): T? {
        val entry = undoStack ?: return null

        undoStack = entry.next
        redoStack = Entry(entry.value, redoStack)

        updateState()

        return entry.value
    }

    fun redo(): T? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        updateState()

        return entry.value
    }

    private fun updateState() {
        _state.value = State(
            canUndo = undoStack != null,
            canRedo = redoStack != null
        )
    }

    private data class Entry<T>(
        val value: T,
        val next: Entry<T>? = null
    )

    data class State(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )
}
