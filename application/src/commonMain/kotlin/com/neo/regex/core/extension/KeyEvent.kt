package com.neo.regex.core.extension

import androidx.compose.ui.input.key.*

val KeyEvent.undo: Boolean
    get() = isCtrlPressed && !isShiftPressed && key == Key.Z

val KeyEvent.redo: Boolean
    get() = isCtrlPressed && isShiftPressed && key == Key.Z
