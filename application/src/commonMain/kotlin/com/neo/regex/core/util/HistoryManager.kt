package com.neo.regex.core.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue

class HistoryManager {

    private var undoStack: Entry? = Entry(TextFieldValue())
    private var redoStack: Entry? = null

    var canUndo by mutableStateOf(false)
        private set

    var canRedo by mutableStateOf(false)
        private set

    private class Entry(
        val value: TextFieldValue,
        val next: Entry? = null
    )

    fun snapshot(value: TextFieldValue) {
        undoStack = Entry(value, undoStack)
        redoStack = null

        canRedo = false
        canUndo = true
    }

    fun undo(): TextFieldValue? {
        val entry = undoStack ?: return null

        undoStack = entry.next
        redoStack = Entry(entry.value, redoStack)

        canRedo = true
        canUndo = undoStack != null

        return entry.value
    }

    fun redo(): TextFieldValue? {
        val entry = redoStack ?: return null

        redoStack = entry.next
        undoStack = Entry(entry.value, undoStack)

        canUndo = true
        canRedo = redoStack != null

        return entry.value
    }
}