package com.neo.regex.core.util

import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.TextFieldValue

val LocalFocusTarget = staticCompositionLocalOf {
    FocusTarget()
}

@Composable
fun rememberTarget() = remember { FocusTarget.Target() }

class FocusTarget {

    var target by mutableStateOf<Target?>(null)

    class Target {

        var text by mutableStateOf(TextFieldValue())
            private set

        val focusRequester = FocusRequester()
        val historyManager = HistoryManager()

        fun update(value: TextFieldValue) {
            text = value
            historyManager.snapshot(value)
        }

        fun undo() {
            historyManager.undo()?.let {
                text = it
            }
        }

        fun redo() {
            historyManager.redo()?.let {
                text = it
            }
        }
    }
}
