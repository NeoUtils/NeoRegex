package com.neo.regex.designsystem.textfield

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun NeoTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    singleLine: Boolean = false,
    hint: (@Composable () -> Unit)? = null
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
            when {
                focused || value.text.isNotEmpty() -> {
                    it()
                }

                hint != null -> {
                    hint()
                }

                else -> {
                    it()
                }
            }
        }
    )
}
