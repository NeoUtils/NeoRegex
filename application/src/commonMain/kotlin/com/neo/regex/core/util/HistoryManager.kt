package com.neo.regex.core.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class HistoryManager<T : Any>(
    private val config: Config<T> = Config()
) {

    private var undoStack: Entry<T>? = null
    private var redoStack: Entry<T>? = null

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var lock = AtomicBoolean(false)

    fun push(value: T) {

        if (lock.get()) return

        if (config.shouldPush(value, undoStack, redoStack)) {

            undoStack = Entry(value, undoStack)
            redoStack = null

            updateState()
            return
        }

        if (config.shouldUpdateLast(value, undoStack, redoStack)) {

            undoStack?.value = value

            updateState()
        }
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

    data class Entry<T>(
        var value: T,
        val next: Entry<T>? = null
    )

    data class State(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false,
    )

    class Config<T : Any>(
        val shouldPush: (T, Entry<T>?, Entry<T>?) -> Boolean = { _, _, _ -> true },
        val shouldUpdateLast: (T, Entry<T>?, Entry<T>?) -> Boolean = { _, _, _ -> false }
    )
}
