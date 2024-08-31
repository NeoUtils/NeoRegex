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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.LineHeightStyle
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.designsystem.theme.Blue100
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
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
                .drawBehind {
                    textLayout?.let { textLayout ->
                        runCatching {
                            matches.flatMap { match ->
                                textLayout.getBoundingBoxes(
                                    match.start,
                                    match.end
                                ).map {
                                    it.deflate(
                                        delta = mergedTextStyle.letterSpacing.toPx()
                                    )
                                }
                            }
                        }.getOrNull()?.forEach {
                            drawRect(
                                color = Blue100,
                                topLeft = Offset(it.left, it.top),
                                size = Size(it.width, it.height)
                            )
                        }
                    }
                }
                .weight(weight = 1f, fill = false)
                .fillMaxSize(),
        )

        VerticalScrollbar(scrollbarAdapter)
    }
}

