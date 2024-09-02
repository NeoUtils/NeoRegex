package com.neo.regex.core.sharedui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.core.sharedui.extension.toText
import com.neo.regex.core.sharedui.extension.tooltip
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.core.sharedui.model.MatchBox
import com.neo.regex.designsystem.theme.Blue100
import com.neo.regex.designsystem.theme.NeoTheme.dimensions

@Composable
actual fun TextEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle,
) {

    val mergedTextStyle = typography.bodyLarge.copy(
        fontFamily = FontFamily.Monospace
    ).merge(textStyle)

    val scrollState = rememberScrollState()

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    val interactionSource = remember { MutableInteractionSource() }

    val textMeasurer = rememberTextMeasurer()

    val hideHandleSelection = remember(value.selection, matches) {
        value.selection.collapsed && matches.any { match ->
            match.range.contains(value.selection.start)
        }
    }

    val defaultTextSelectionColors = LocalTextSelectionColors.current

    val textSelectionColors = remember(
        hideHandleSelection,
        defaultTextSelectionColors
    ) {
        TextSelectionColors(
            handleColor = if (hideHandleSelection) {
                Color.Transparent
            } else {
                defaultTextSelectionColors.handleColor
            },
            backgroundColor = defaultTextSelectionColors.backgroundColor,
        )
    }

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
                )
            ),
            modifier = Modifier
                .background(Color.LightGray.copy(alpha = 0.4f))
                .fillMaxHeight()
        )


        CompositionLocalProvider(LocalTextSelectionColors provides textSelectionColors) {
            // TODO(improve): it's not performant for large text
            BasicTextField(
                value = value.copy(
                    composition = null
                ),
                onValueChange = onValueChange,
                textStyle = mergedTextStyle.copy(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Proportional,
                        trim = LineHeightStyle.Trim.None
                    )
                ),
                interactionSource = interactionSource,
                modifier = Modifier
                    .padding(start = dimensions.tiny)
                    .weight(weight = 1f, fill = false)
                    .fillMaxSize()
                    .verticalScroll(scrollState) // TODO(improve): https://github.com/NeoUtils/NeoRegex/issues/15
                    .onFocusChanged(onFocusChange)
                    .drawWithContent {
                        val matchBoxes = textLayout?.let { textLayout ->
                            matches.flatMap { match ->
                                textLayout
                                    .getBoundingBoxes(
                                        match.range.first,
                                        match.range.last
                                    )
                                    .map {
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
                                color = Blue100,
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, rect.height)
                            )
                        }

                        val selectedMatchBox = value.selection
                            .takeIf {
                                it.collapsed
                            }
                            ?.let { selection ->
                                matchBoxes.firstOrNull { (match, _) ->
                                    match.range.contains(selection.start)
                                }
                            }

                        selectedMatchBox?.let { (_, rect) ->

                            drawRect(
                                color = Color.DarkGray,
                                topLeft = Offset(rect.left, y = rect.top - scrollState.value),
                                size = Size(rect.width, rect.height),
                                style = Stroke(
                                    width = 1f
                                )
                            )
                        }

                        drawContent()

                        selectedMatchBox?.let { (match, rect) ->
                            tooltip(
                                anchorRect = rect
                                    .inflate(
                                        delta = 0.8f
                                    )
                                    .let {
                                        Rect(
                                            left = rect.left,
                                            top = it.top - scrollState.value,
                                            right = rect.right,
                                            bottom = it.bottom - scrollState.value
                                        )
                                    },
                                measure = textMeasurer.measure(
                                    text = match.toText(),
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            )
                        }
                    },
                onTextLayout = {
                    textLayout = it
                }
            )
        }
    }
}
