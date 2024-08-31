package com.neo.regex.core.extension

import androidx.compose.ui.input.key.*

val KeyEvent.isUndoPressed: Boolean
    get() = isCtrlPressed &&
            !isShiftPressed &&
            type == KeyEventType.KeyDown &&
            key == Key.Z

val KeyEvent.isRedoPressed: Boolean
    get() = isCtrlPressed &&
            isShiftPressed &&
            type == KeyEventType.KeyDown &&
            key == Key.Z
