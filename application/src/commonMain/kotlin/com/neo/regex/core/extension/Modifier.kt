package com.neo.regex.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.*
import com.neo.regex.core.util.FocusTarget
import com.neo.regex.core.util.LocalFocusTarget

@Composable
fun Modifier.attachFocusTarget(
    target: FocusTarget.Target,
    focusTarget: FocusTarget = LocalFocusTarget.current
) = onFocusChanged {
    if (it.isFocused) {
        focusTarget.target = target
    }
}.focusRequester(
    target.focusRequester
).onPreviewKeyEvent {
    when {
        it.undo -> {
            target.undo()
            true
        }

        it.redo -> {
            target.redo()
            true
        }

        else -> false
    }
}
