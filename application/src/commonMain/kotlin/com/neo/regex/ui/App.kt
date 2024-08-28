package com.neo.regex.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.neo.regex.design.textfield.NeoTextField
import com.neo.regex.design.theme.NeoTheme
import com.neo.regex.design.theme.NeoTheme.dimensions

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
            .padding(dimensions.medium)
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
            modifier = Modifier.fillMaxWidth()
        )
    }
}
