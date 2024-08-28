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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.neo.regex.core.sharedui.TextEditor
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

    var text by remember { mutableStateOf(TextFieldValue()) }

    TextEditor(
        value = text,
        onValueChange = {
            text = it
        },
        modifier = Modifier
            .weight(weight = 1f)
    )

    Footer(Modifier.fillMaxWidth())
}

@Composable
private fun Footer(
    modifier: Modifier = Modifier
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        var regex by remember { mutableStateOf("") }

        NeoTextField(
            value = regex,
            onValueChange = {
                regex = it
            },
            hint = {
                Text(
                    text = stringResource(Res.string.insert_regex_hint),
                    style = typography.bodyLarge
                )
            },
            singleLine = true,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(dimensions.default)
                .weight(weight = 1f)
        )

        HistoryControl(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(dimensions.small)
        )
    }
}


@Composable
private fun HistoryControl(
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(dimensions.small),
    onUndo: () -> Unit = {},
    onRedo: () -> Unit = {}
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min)
        .border(
            width = 1.dp,
            color = colorScheme.outline,
            shape = RoundedCornerShape(dimensions.small)
        )
) {
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
            .clickable(onClick = onUndo)
            .padding(
                vertical = dimensions.tiny,
                horizontal = dimensions.small,
            )
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
            .clickable(onClick = onRedo)
            .padding(
                vertical = dimensions.tiny,
                horizontal = dimensions.small,
            )
    )
}