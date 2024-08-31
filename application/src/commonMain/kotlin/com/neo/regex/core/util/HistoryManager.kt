package com.neo.regex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class HistoryManager<T> {

    private var undoStack: Entry<T>? = null
    private var redoStack: Entry<T>? = null

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var lock = AtomicBoolean(false)

    fun push(value: T) {

        if (lock.get()) return

        undoStack = Entry(value, undoStack)
        redoStack = null

        updateState()
    }

    fun undo(): T? {
        val entry = undoStack ?: return null

        undoStack = entry.next ?: return null
        redoStack = Entry(entry.value, redoStack)

        updateState()

        lock.set(true)

        return undoStack?.value
    }

    fun redo(): T? {
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

    private data class Entry<T>(
        val value: T,
        val next: Entry<T>? = null
    )

    data class State(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
    )
}
