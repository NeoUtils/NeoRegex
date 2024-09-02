package com.neo.regex.core.sharedui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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

    var pressedMatch by remember { mutableStateOf<Match?>(null) }

    val interactionSource = remember { MutableInteractionSource() }

    var selectedMatch by remember { mutableStateOf<Match?>(null) }

    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(matches) {
        selectedMatch = null
    }

    LaunchedEffect(interactionSource, matches) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    pressedMatch = textLayout?.let { textLayout ->
                        matches.firstOrNull { match ->
                            textLayout
                                .getBoundingBoxes(
                                    match.range.first,
                                    match.range.last
                                )
                                .any {
                                    it.contains(interaction.pressPosition)
                                }
                        }
                    }
                    selectedMatch = null
                }

                is PressInteraction.Release -> {
                    selectedMatch = pressedMatch
                }

                is PressInteraction.Cancel -> {
                    pressedMatch = null
                    selectedMatch = null
                }
            }
        }
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


        val defaultTextSelectionColors = LocalTextSelectionColors.current

        val textSelectionColors = remember(selectedMatch) {
            TextSelectionColors(
                handleColor = if (selectedMatch != null) {
                    Color.Transparent
                } else {
                    defaultTextSelectionColors.handleColor
                },
                backgroundColor = defaultTextSelectionColors.backgroundColor,
            )
        }

        CompositionLocalProvider(
            LocalTextSelectionColors provides textSelectionColors
        ) {
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
                            runCatching {
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
                            }.getOrNull()
                        } ?: listOf()

                        matchBoxes.forEach { (_, rect) ->
                            drawRect(
                                color = Blue100,
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, rect.height)
                            )
                        }

                        pressedMatch?.let { match ->
                            val matchBox = matchBoxes.firstOrNull { (it, _) ->
                                it == match
                            }

                            matchBox?.let { (_, rect) ->
                                drawRect(
                                    color = Color.DarkGray,
                                    topLeft = Offset(rect.left, y = rect.top - scrollState.value),
                                    size = Size(rect.width, rect.height),
                                    style = Stroke(
                                        width = 1f
                                    )
                                )
                            }
                        }

                        drawContent()

                        selectedMatch?.let { match ->
                            val matchBox = matchBoxes.firstOrNull { (it, _) ->
                                it == match
                            }

                            matchBox?.let { (_, rect) ->

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
                        }
                    },
                onTextLayout = {
                    textLayout = it
                }
            )
        }
    }
}
