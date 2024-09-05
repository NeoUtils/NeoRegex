package com.neo.regex.core.util

import androidx.compose.ui.input.key.*

enum class Command(
    val modifies: List<Special>,
    val key: Key,
    val type: KeyEventType = KeyEventType.KeyDown
) {
    UNDO(
        modifies = listOf(Special.CTRL),
        key = Key.Z
    ),
    REDO(
        modifies = listOf(
            Special.CTRL,
            Special.SHIFT
        ),
        key = Key.Z
    );

    companion object {
        fun from(keyEvent: KeyEvent): Command? {

            val modifies = Special.from(keyEvent)

            return entries.find {
                it.modifies == modifies &&
                        it.key == keyEvent.key &&
                        it.type == keyEvent.type
            }
        }
    }
}