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

package com.neo.regex.core.sharedui.extension

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

    val topLeft = if (drawAbove) {
        Offset(
            x = anchorRect.center.x - tooltipSize.width / 2,
            y = anchorRect.top - tooltipSize.height - triangleHeightPx
        )
    } else {
        Offset(
            x = anchorRect.center.x - tooltipSize.width / 2,
            y = anchorRect.bottom + triangleHeightPx
        )
    }

    drawPath(
        path = Path().apply {
            if (drawAbove) {
                moveTo(anchorRect.center.x, anchorRect.top)
                lineTo(
                    x = anchorRect.center.x - triangleHeightPx,
                    y = anchorRect.top - triangleHeightPx
                )
                lineTo(
                    x = anchorRect.center.x + triangleHeightPx,
                    y = anchorRect.top - triangleHeightPx
                )
                close()
            } else {
                moveTo(anchorRect.center.x, anchorRect.bottom)
                lineTo(
                    x = anchorRect.center.x - triangleHeightPx,
                    y = anchorRect.bottom + triangleHeightPx
                )
                lineTo(
                    x = anchorRect.center.x + triangleHeightPx,
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
