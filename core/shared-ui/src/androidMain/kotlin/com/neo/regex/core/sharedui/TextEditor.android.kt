package com.neo.regex.core.sharedui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import com.neo.regex.core.sharedui.extension.getBoundingBoxes
import com.neo.regex.core.sharedui.model.Match
import com.neo.regex.designsystem.theme.Blue100
import com.neo.regex.designsystem.theme.NeoTheme.dimensions

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun TextEditor(
    value: String,
    onValueChange: (String) -> Unit,
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

        BasicTextField2(
            value = value,
            onValueChange = onValueChange,
            scrollState = scrollState,
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                )
            ),
            onTextLayout = {
                textLayout = it()
            },
            modifier = Modifier
                .padding(start = dimensions.tiny)
                .weight(weight = 1f, fill = false)
                .fillMaxSize()
                .onFocusChanged(onFocusChange)
                .drawBehind {
                    textLayout?.let { textLayout ->
                        val boxes = runCatching {
                            matches.flatMap { match ->
                                textLayout
                                    .getBoundingBoxes(
                                        match.start,
                                        match.end
                                    )
                                    .map {
                                        it.deflate(
                                            delta = mergedTextStyle.letterSpacing.toPx()
                                        )
                                    }
                            }
                        }.getOrElse { listOf() }

                        inset(vertical = scrollState.value * -1f) {
                            boxes.forEach {
                                drawRect(
                                    color = Blue100,
                                    topLeft = Offset(it.left, it.top),
                                    size = Size(it.width, it.height)
                                )
                            }
                        }
                    }
                },
        )
    }
}