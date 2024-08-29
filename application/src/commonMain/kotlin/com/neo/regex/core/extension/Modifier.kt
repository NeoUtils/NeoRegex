package com.neo.regex.core.extension

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
