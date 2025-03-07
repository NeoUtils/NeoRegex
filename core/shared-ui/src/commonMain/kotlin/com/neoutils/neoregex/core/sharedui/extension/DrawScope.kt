/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.neoutils.neoregex.core.sharedui.extension

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun DrawScope.tooltip(
    anchorRect: Rect,
    measure: TextLayoutResult,
    backgroundColor: Color,
    padding: Dp = 8.dp,
    cornerRadius: Dp = 4.dp,
    triangleHeight: Dp = 8.dp
) {
    val paddingPx = padding.toPx()
    val cornerRadiusPx = cornerRadius.toPx()
    val triangleHeightPx = triangleHeight.toPx()

    val tooltipSize = Size(
        width = measure.size.width + 2 * paddingPx,
        height = measure.size.height + 2 * paddingPx
    )

    val drawAbove = anchorRect.bottom + triangleHeightPx + tooltipSize.height > size.height

    // Calculate horizontal position, ensuring tooltip stays within screen bounds
    var xPosition = anchorRect.center.x - tooltipSize.width / 2
    
    // Adjust x position if tooltip would extend beyond left edge
    if (xPosition < 0) {
        xPosition = 0f
    }
    
    // Adjust x position if tooltip would extend beyond right edge
    if (xPosition + tooltipSize.width > size.width) {
        xPosition = size.width - tooltipSize.width
    }
    
    val topLeft = if (drawAbove) {
        Offset(
            x = xPosition,
            y = anchorRect.top - tooltipSize.height - triangleHeightPx
        )
    } else {
        Offset(
            x = xPosition,
            y = anchorRect.bottom + triangleHeightPx
        )
    }

    // Calculate triangle position to ensure it aligns with the anchor
    // but doesn't exceed tooltip bounds and respects corner radius
    val triangleX = anchorRect.center.x.coerceIn(
        xPosition + cornerRadiusPx + triangleHeightPx,
        xPosition + tooltipSize.width - cornerRadiusPx - triangleHeightPx
    )

    drawPath(
        path = Path().apply {
            if (drawAbove) {
                moveTo(triangleX, anchorRect.top)
                lineTo(
                    x = triangleX - triangleHeightPx,
                    y = anchorRect.top - triangleHeightPx
                )
                lineTo(
                    x = triangleX + triangleHeightPx,
                    y = anchorRect.top - triangleHeightPx
                )
                close()
            } else {
                moveTo(triangleX, anchorRect.bottom)
                lineTo(
                    x = triangleX - triangleHeightPx,
                    y = anchorRect.bottom + triangleHeightPx
                )
                lineTo(
                    x = triangleX + triangleHeightPx,
                    y = anchorRect.bottom + triangleHeightPx
                )
                close()
            }
        },
        color = backgroundColor
    )

    drawRoundRect(
        color = backgroundColor,
        topLeft = topLeft,
        size = tooltipSize,
        cornerRadius = CornerRadius(cornerRadiusPx)
    )

    drawText(
        textLayoutResult = measure,
        topLeft = topLeft + Offset(paddingPx, paddingPx),
    )
}
