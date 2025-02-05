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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = ""
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(text = value)) }

    NeoTextField(
        value = textFieldValue.copy(
            text = value
        ),
        onValueChange = {
            textFieldValue = it
            onValueChange(it.text)
        },
        onTextLayout = onTextLayout,
        modifier = modifier,
        textStyle = textStyle,
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
    singleLine: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = ""
) {

    val mergedTextStyle = typography.bodyLarge.copy(
        color = LocalContentColor.current
    ).merge(textStyle)

    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = singleLine,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(mergedTextStyle.color),
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
        },
        onTextLayout = onTextLayout,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.padding(contentPadding),
                propagateMinConstraints = true
            ) {

                innerTextField()

                if (value.text.isEmpty()) {
                    Text(
                        text = hint.substringBefore(delimiter = "\n"),
                        style = mergedTextStyle.copy(
                            color = mergedTextStyle.color.copy(
                                alpha = 0.5f
                            )
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        },
    )
}
