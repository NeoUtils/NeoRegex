package com.neo.regex.core.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import com.neo.regex.core.util.FocusTarget
import com.neo.regex.core.util.LocalFocusTarget
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

fun Modifier.onLongHold(
    delayMillis: Long = 10L,
    action: () -> Unit
) = composed {

    val scope = rememberCoroutineScope()

    pointerInput(Unit) {

        var job: Job? = null

        detectPressEvent(
            onRelease = {
                job?.cancel()
            },
            onLongPress = {
                job = scope.launch {
                    while (true) {
                        action()
                        delay(delayMillis)
                    }
                }
            },
        )
    }
}
