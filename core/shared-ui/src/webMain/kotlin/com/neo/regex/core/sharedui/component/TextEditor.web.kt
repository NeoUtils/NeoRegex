/*
 * NeoRegex.
 *
 * Copyright (C) 2024 <AUTHOR>.
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

package com.neo.regex.core.sharedui.component

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.rememberTextFieldVerticalScrollState
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import com.neo.regex.core.designsystem.theme.NeoTheme.dimensions
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.core.sharedui.extension.toText
import com.neo.regex.core.sharedui.extension.tooltip
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.core.sharedui.model.MatchBox
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun TextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle
) {

    val mergedTextStyle = typography.bodyMedium.copy(
        fontFamily = FontFamily.Monospace,
    ).merge(textStyle)

    val scrollState = rememberScrollState()

    val scrollbarAdapter = rememberScrollbarAdapter(scrollState)

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    var hoverOffset by remember { mutableStateOf<Offset?>(null) }

    val textMeasurer = rememberTextMeasurer()

    val colorScheme = colorScheme

    Row(modifier) {

        LineNumbers(
            count = textLayout?.lineCount ?: 1,
            offset = scrollState.value,
            textStyle = TextStyle(
                lineHeight = mergedTextStyle.lineHeight,
                fontSize = mergedTextStyle.fontSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .background(colorScheme.surfaceVariant)
                .fillMaxHeight()
        )

        // TODO(improve): it's not performant for large text
        BasicTextField(
            value = value.copy(
                composition = null,
            ),
            onValueChange = onValueChange,
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurface,
            ),
            cursorBrush = SolidColor(colorScheme.onSurface),
            modifier = Modifier
                .background(colorScheme.surface)
                .onFocusChanged(onFocusChange)
                .padding(start = dimensions.tiny)
                .weight(weight = 1f, fill = false)
                .fillMaxSize()
                .verticalScroll(scrollState) // TODO(improve): https://github.com/NeoUtils/NeoRegex/issues/15
                .onPointerEvent(PointerEventType.Move) { event ->
                    hoverOffset = event.changes.first().position.let {
                        it.copy(y = it.y)
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
                                MatchBox(
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
                            color = colorScheme.secondary,
                            topLeft = Offset(
                                x = rect.left,
                                y = rect.top
                            ),
                            size = Size(rect.width, rect.height)
                        )
                    }

                    drawContent()

                    hoverOffset?.let { offset ->
                        val matchBox = matchBoxes.firstOrNull { (_, rect) ->
                            rect.contains(offset)
                        }

                        matchBox?.let { (match, rect) ->
                            drawRect(
                                color = colorScheme.onSurface,
                                topLeft = Offset(
                                    x = rect.left,
                                    y = rect.top
                                ),
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
                                        left = offset.x,
                                        top = it.top ,
                                        right = offset.x,
                                        bottom = it.bottom
                                    )
                                },
                                measure = textMeasurer.measure(
                                    text = match.toText(),
                                    style = TextStyle(
                                        color = colorScheme.onSecondaryContainer,
                                        fontFamily = FontFamily.Monospace,
                                    )
                                ),
                                backgroundColor = colorScheme.secondaryContainer,
                            )
                        }
                    }
                },
            onTextLayout = {
               textLayout = it
            },
        )

        VerticalScrollbar(scrollbarAdapter)
    }
}