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

package com.neoutils.neoregex.core.designsystem.textfield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.neoutils.neoregex.core.common.extension.getBoundingBoxes
import com.neoutils.neoregex.core.common.model.DrawMatch
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    matches: List<Match> = listOf(),
    matchColor: Color = colorScheme.secondary,
    singleLine: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: @Composable () -> Unit = {}
) {
    var selection by remember { mutableStateOf(TextRange(value.length)) }

    NeoTextField(
        value = TextFieldValue(
            value,
            selection
        ),
        onValueChange = {
            selection = it.selection
            onValueChange(it.text)
        },
        onTextLayout = onTextLayout,
        modifier = modifier,
        textStyle = textStyle,
        keyboardActions = keyboardActions,
        matches = matches,
        matchesColor = matchColor,
        singleLine = singleLine,
        contentPadding = contentPadding,
        hint = hint
    )
}

@Composable
fun NeoTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    matches: List<Match> = listOf(),
    matchesColor: Color = colorScheme.secondary,
    singleLine: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: @Composable () -> Unit = {}
) {
    val mergedTextStyle = typography.bodyLarge.copy(
        color = LocalContentColor.current
    ).merge(textStyle)

    var focused by remember { mutableStateOf(false) }

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    ProvideTextStyle(mergedTextStyle) {
        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            singleLine = singleLine,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(mergedTextStyle.color),
            keyboardActions = keyboardActions,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = modifier.onFocusChanged {
                focused = it.isFocused
            },
            onTextLayout = {
                onTextLayout(it)
                textLayout = it
            },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(contentPadding)
                        .drawBehind {
                            val matchBoxes = textLayout?.let { textLayout ->
                                matches.flatMap { match ->
                                    textLayout.getBoundingBoxes(
                                        match.range.first,
                                        match.range.last
                                    ).map {
                                        DrawMatch(
                                            match,
                                            listOf(
                                                it.deflate(
                                                    delta = 0.8f
                                                )
                                            )
                                        )
                                    }
                                }
                            }

                            matchBoxes?.forEach { (_, rect) ->
                                drawRect(
                                    color = matchesColor,
                                    topLeft = Offset(
                                        x = rect[0].left,
                                        y = rect[0].top
                                    ),
                                    size = Size(
                                        rect[0].width,
                                        rect[0].height
                                    )
                                )
                            }
                        },
                    propagateMinConstraints = true
                ) {

                    innerTextField()

                    if (value.text.isEmpty()) {
                        hint()
                    }
                }
            },
        )
    }
}
