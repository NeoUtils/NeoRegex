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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import kotlinx.coroutines.launch


@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    endIcon: @Composable (() -> Unit)? = null,
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
        endIcon = endIcon,
        modifier = modifier,
        textStyle = textStyle,
        singleLine = singleLine,
        contentPadding = contentPadding,
        hint = hint
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    endIcon: @Composable (() -> Unit)? = null,
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
        decorationBox = { innerTextField ->
            TextFieldDefaults.DecorationBox(
                value = value.text,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = singleLine,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                contentPadding = contentPadding,
                isError = false,
                container = {},
                placeholder = {
                    Text(
                        text = hint,
                        style = mergedTextStyle.copy(
                            color = mergedTextStyle.color.copy(
                                alpha = 0.5f
                            )
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                },
                trailingIcon = endIcon
            )
        },
    )
}
