package com.neo.regex.feature.matcher.extension

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.PointerEventTimeoutCancellationException
import androidx.compose.ui.input.pointer.PointerInputScope

suspend fun PointerInputScope.detectPressEvent(
    onPress: (() -> Unit)? = null,
    onLongPress: (() -> Unit)? = null,
    onRelease: (() -> Unit)? = null,
) {

    awaitEachGesture {

        awaitFirstDown()

        onPress?.invoke()

        val longPressTimeout = onLongPress?.let {
            viewConfiguration.longPressTimeoutMillis
        } ?: (Long.MAX_VALUE / 2)

        try {
            withTimeout(longPressTimeout) {
                waitForUpOrCancellation()
            }

            onRelease?.invoke()
        } catch (_: PointerEventTimeoutCancellationException) {
            onLongPress?.invoke()
        }

        waitForUpOrCancellation()

        onRelease?.invoke()
    }
}