package com.neo.regex.design.textfield

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle

@Composable
fun NeoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    hint: (@Composable () -> Unit)? = null
) {

    val mergedTextStyle = typography.bodyLarge.merge(textStyle)

    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        modifier = modifier
            .onFocusChanged {
                focused = it.isFocused
            },
        decorationBox = {
            when {
                focused || value.isNotEmpty() -> {
                    it()
                }

                hint != null -> {
                    hint()
                }

                else -> {
                    it()
                }
            }
        },
        textStyle = mergedTextStyle
    )
}
