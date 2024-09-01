package com.neo.regex.core.sharedui.extension

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult

fun TextLayoutResult.getBoundingBoxes(start: Int, end: Int): List<Rect> {

    val boxes = mutableListOf<Rect>()

    var lastRect: Rect? = null
    var lastLine: Int? = null

    for (offset in start .. end) {

        var rect = getBoundingBox(offset)
        val line = getLineForOffset(offset)

        if (lastRect != null && lastLine == line) {
            rect = lastRect.union(rect)
            boxes.remove(lastRect)
        }

        if (lastRect != null && lastLine != line) {

            boxes.add(
                lastRect.copy(
                    left = size.width.toFloat()
                )
            )

            boxes.remove(lastRect)
        }

        lastLine = line
        lastRect = rect
        boxes.add(rect)
    }
    return boxes
}
