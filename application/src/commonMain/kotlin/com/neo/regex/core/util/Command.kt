package com.neo.regex.core.util

import androidx.compose.ui.input.key.KeyEvent
import com.neo.regex.core.extension.redo
import com.neo.regex.core.extension.undo

enum class Command {
    UNDO,
    REDO;

    companion object {
        fun from(keyEvent: KeyEvent): Command? = when {
            keyEvent.undo -> UNDO
            keyEvent.redo -> REDO
            else -> null
        }
    }
}