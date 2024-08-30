package com.neo.regex.core.sharedui.extension

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult

fun TextLayoutResult.getBoundingBoxes(start: Int, end: Int): List<Rect> {

    val boundingBoxes = mutableListOf<Rect>()

    var lastRect: Rect? = null

    for (offset in start until end) {

        var rect = getBoundingBox(offset)

        if (lastRect?.top == rect.top) {
            rect = lastRect.union(rect)
            boundingBoxes.remove(lastRect)
        }

        lastRect = rect
        boundingBoxes.add(rect)
    }
    return boundingBoxes
}
