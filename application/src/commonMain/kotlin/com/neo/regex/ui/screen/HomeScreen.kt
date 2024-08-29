package com.neo.regex.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import com.neo.regex.core.extension.attachFocusTarget
import com.neo.regex.core.sharedui.TextEditor
import com.neo.regex.core.util.LocalFocusTarget
import com.neo.regex.core.util.rememberTarget
import com.neo.regex.designsystem.textfield.NeoTextField
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import com.neo.regex.resources.Res
import com.neo.regex.resources.ic_redo_24
import com.neo.regex.resources.ic_undo_24
import com.neo.regex.resources.insert_regex_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() = Column(
    modifier = Modifier
        .background(colorScheme.background)
        .fillMaxSize()
) {

    val textTarget = rememberTarget()
    val focusTarget = LocalFocusTarget.current

    TextEditor(
        value = textTarget.text,
        onValueChange = {
            textTarget.update(it)
        },
        modifier = Modifier
            .attachFocusTarget(textTarget)
            .weight(weight = 1f),
        onFocusChange = {
            focusTarget.target = textTarget
        }
    )

    Footer(
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier,
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        val regexTarget = rememberTarget()

        LaunchedEffect(Unit) {
            regexTarget.focusRequester.requestFocus()
        }

        NeoTextField(
            value = regexTarget.text,
            onValueChange = {
                regexTarget.update(it)
            },
            hint = {
                Text(
                    text = stringResource(Res.string.insert_regex_hint),
                    style = typography.bodyLarge
                )
            },
            singleLine = true,
            modifier = Modifier
                .attachFocusTarget(regexTarget)
                .align(Alignment.CenterVertically)
                .padding(dimensions.default)
                .weight(weight = 1f)
        )

        HistoryControl(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(dimensions.small)
                .focusProperties {
                    canFocus = false
                },
        )
    }
}


@Composable
private fun HistoryControl(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(dimensions.small),
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min)
        .border(
            width = 1.dp,
            color = colorScheme.outline,
            shape = RoundedCornerShape(dimensions.small)
        )
) {

    val focusTarget = LocalFocusTarget.current
    val historyManager = focusTarget.target?.historyManager

    Icon(
        painter = painterResource(Res.drawable.ic_undo_24),
        contentDescription = null,
        modifier = Modifier
            .clip(
                shape.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            )
            .clickable(
                enabled = historyManager?.canUndo == true,
                onClick = {
                    focusTarget.target?.undo()
                }
            )
            .padding(
                vertical = dimensions.tiny,
                horizontal = dimensions.small,
            ),
        tint = if (historyManager?.canUndo == true) {
            colorScheme.onSurfaceVariant
        } else {
            colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        }
    )

    VerticalDivider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
            .weight(
                weight = 1f,
                fill = false
            )
    )

    Icon(
        painter = painterResource(Res.drawable.ic_redo_24),
        contentDescription = null,
        modifier = Modifier
            .clip(
                shape.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
            )
            .clickable(
                enabled = historyManager?.canRedo == true,
                onClick = {
                    focusTarget.target?.redo()
                }
            )
            .padding(
                vertical = dimensions.tiny,
                horizontal = dimensions.small,
            ),
        tint = if (historyManager?.canRedo == true) {
            colorScheme.onSurfaceVariant
        } else {
            colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        }
    )
}