package com.neo.regex.core.util

import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed

enum class Special {
    CTRL,
    SHIFT,
    ALT;

    companion object {
        fun from(keyEvent: KeyEvent) = buildList {
            if (keyEvent.isCtrlPressed) {
                add(CTRL)
            }
            if (keyEvent.isShiftPressed) {
                add(SHIFT)
            }
            if (keyEvent.isAltPressed) {
                add(ALT)
            }
        }
    }
}