package com.neo.regex.designsystem.textfield

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.neo.regex.designsystem.theme.NeoTheme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    contentPadding: PaddingValues = PaddingValues(dimensions.default),
    hint: String = "",
) {

    val mergedTextStyle = typography.bodyLarge.merge(textStyle)

    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = singleLine,
        textStyle = mergedTextStyle,
        modifier = modifier.onFocusChanged {
            focused = it.isFocused
        },
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = value.text,
                innerTextField = it,
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
                        style = typography.bodyLarge.copy(
                            color = Color.Gray
                        )
                    )
                },
            )
        },
    )
}
