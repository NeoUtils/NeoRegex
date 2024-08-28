package com.neo.regex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.neo.regex.design.textfield.NeoTextField
import com.neo.regex.design.theme.NeoTheme
import com.neo.regex.design.theme.NeoTheme.dimensions
import com.neo.resources.Res
import com.neo.resources.ic_redo_24
import com.neo.resources.ic_undo_24
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    NeoTheme(darkMode = false) {
        HomeScreen()
    }
}

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
        modifier = Modifier.weight(1f)
    )

    Footer(Modifier.fillMaxWidth())
}

@Composable
fun Footer(
    modifier: Modifier = Modifier
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = 8.dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        var regex by remember { mutableStateOf("") }

        NeoTextField(
            value = regex,
            onValueChange = {
                regex = it
            },
            hint = {
                Text(
                    text = "Enter regex",
                    style = typography.bodyLarge
                )
            },
            modifier = Modifier.padding(
                dimensions.medium
            ).weight(weight = 1f)
        )

        HistoryControl(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(
                    dimensions.small
                )
        )
    }
}

@Composable
fun HistoryControl(
    modifier: Modifier = Modifier
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
                RoundedCornerShape(
                    topStart = dimensions.small,
                    bottomStart = dimensions.small
                )
            ).clickable { }
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp,
            )
    )

    VerticalDivider(
        modifier = Modifier
            .width(1.dp)
            .weight(weight = 1f, fill = false)
            .fillMaxHeight()
    )

    Icon(
        painter = painterResource(Res.drawable.ic_redo_24),
        contentDescription = null,
        modifier = Modifier
            .clip(
                RoundedCornerShape(
                    topEnd = dimensions.small,
                    bottomEnd = dimensions.small
                )
            )
            .clickable { }
            .padding(
                vertical = 4.dp,
                horizontal = 8.dp,
            )
    )
}