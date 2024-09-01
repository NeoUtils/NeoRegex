package com.neo.regex.core.sharedui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.rememberTextFieldVerticalScrollState
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.designsystem.theme.Blue100
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
actual fun TextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle,
) {

    val mergedTextStyle = typography.bodyMedium.copy(
        fontFamily = FontFamily.Monospace,
    ).merge(textStyle)

    val scrollState = rememberTextFieldVerticalScrollState()

    val scrollbarAdapter = rememberScrollbarAdapter(scrollState)

    val offset = remember(scrollState.offset) { scrollState.offset.roundToInt() }

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    val textMeasurer = rememberTextMeasurer()

    Row(modifier) {

        LineNumbers(
            count = textLayout?.lineCount ?: 1,
            offset = offset,
            textStyle = TextStyle(
                lineHeight = mergedTextStyle.lineHeight,
                fontSize = mergedTextStyle.fontSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            modifier = Modifier.background(
                color = Color.LightGray.copy(alpha = 0.4f)
            ).fillMaxHeight()
        )

        // TODO(improve): it's not performant for large text
        BasicTextField(
            value = value.copy(
                composition = null
            ),
            scrollState = scrollState,
            onValueChange = onValueChange,
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            onTextLayout = {
                textLayout = it
            },
            modifier = Modifier
                .onFocusChanged(onFocusChange)
                .padding(start = dimensions.tiny)
                .weight(weight = 1f, fill = false)
                .fillMaxSize()
                .onPointerEvent(PointerEventType.Move) { event ->
                    hoverOffset = event.changes.first().position.let {
                        it.copy(y = it.y + scrollState.offset)
                    }
                }
                .onPointerEvent(PointerEventType.Exit) {
                    hoverOffset = null
                }
                .drawWithContent {
                    val matchBoxes = textLayout?.let { textLayout ->
                        matches.flatMap { match ->
                            textLayout.getBoundingBoxes(
                                match.range.first,
                                match.range.last
                            ).map {
                                Pair(
                                    match,
                                    it.deflate(
                                        delta = 0.8f
                                    )
                                )
                            }
                        }
                    } ?: listOf()

                    matchBoxes.forEach { (_, rect) ->
                        drawRect(
                            color = Blue100,
                            topLeft = Offset(rect.left, y = rect.top - scrollState.offset),
                            size = Size(rect.width, rect.height)
                        )
                    }

                    drawContent()

                    hoverOffset?.let { hoverOffset ->
                        val matchBox = matchBoxes.firstOrNull { (_, rect) ->
                            rect.contains(hoverOffset)
                        }

                        matchBox?.let { (match, rect) ->
                            drawRect(
                                color = Color.DarkGray,
                                topLeft = Offset(rect.left, y = rect.top - scrollState.offset),
                                size = Size(rect.width, rect.height),
                                style = Stroke(
                                    width = 1f
                                )
                            )

                            tooltip(
                                anchorRect = rect.inflate(
                                    delta = 0.8f
                                ).let {
                                    Rect(
                                        left = hoverOffset.x,
                                        top = it.top - scrollState.offset,
                                        right = hoverOffset.x,
                                        bottom = it.bottom - scrollState.offset
                                    )
                                },
                                measure = textMeasurer.measure(
                                    text = match.toString(),
                                    style= TextStyle(
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            )
                        }
                    }
                },
        )

        VerticalScrollbar(scrollbarAdapter)
    }
}

fun DrawScope.tooltip(
    anchorRect: Rect,
    measure: TextLayoutResult,
    backgroundColor: Color = Color.DarkGray,
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
