package com.neo.regex.core.designsystem.textfield

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
import com.neo.regex.core.designsystem.theme.NeoTheme.dimensions
import kotlinx.coroutines.launch

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
    error: String = ""
) {

    val contentColor = LocalContentColor.current

    val mergedTextStyle = typography.bodyLarge.copy(
        color = contentColor
    ).merge(textStyle)

    var focused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        singleLine = singleLine,
        textStyle = mergedTextStyle,
        cursorBrush = SolidColor(contentColor),
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
                        style = mergedTextStyle.copy(
                            color = mergedTextStyle.color.copy(
                                alpha = 0.5f
                            )
                        )
                    )
                },
                trailingIcon = {
                    if (error.isNotEmpty()) {

                        val colorScheme = colorScheme

                        val tooltipState = rememberTooltipState(isPersistent = true)
                        val scope = rememberCoroutineScope()

                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                            tooltip = {
                                PlainTooltip(
                                    containerColor = colorScheme.secondaryContainer,
                                    contentColor = colorScheme.onSecondaryContainer,
                                ) {
                                    Text(error)
                                }
                            },
                            state = tooltipState,
                            focusable = false,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Info,
                                contentDescription = error,
                                tint = colorScheme.error,
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {
                                        scope.launch {
                                            tooltipState.show()
                                        }
                                    }
                                )
                            )
                        }
                    }
                },
            )
        },
    )
}
