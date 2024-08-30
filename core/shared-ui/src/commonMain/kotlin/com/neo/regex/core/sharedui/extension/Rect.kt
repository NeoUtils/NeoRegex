package com.neo.regex.core.sharedui.extension

import androidx.compose.ui.geometry.Rect

fun Rect.union(rect: Rect): Rect {
    return Rect(
        left = left.coerceAtMost(rect.left),
        top = top.coerceAtMost(rect.top),
        right = right.coerceAtLeast(rect.right),
        bottom = bottom.coerceAtLeast(rect.bottom)
    )
}
