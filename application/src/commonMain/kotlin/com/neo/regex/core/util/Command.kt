package com.neo.regex.core.util

import androidx.compose.ui.input.key.KeyEvent
import com.neo.regex.core.extension.isRedoPressed
import com.neo.regex.core.extension.isUndoPressed

enum class Command {
    UNDO,
    REDO;

    companion object {
        fun from(keyEvent: KeyEvent): Command? = when {
            keyEvent.isUndoPressed -> UNDO
            keyEvent.isRedoPressed -> REDO
            else -> null
        }
    }
}