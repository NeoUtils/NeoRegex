package com.neo.regex.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.neo.regex.designsystem.theme.NeoTheme.dimensions
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton

@Composable
fun Buttons(
    modifier: Modifier = Modifier,
    buttons: List<Button>
) = Row(modifier) {
    buttons.forEach { button ->
        IconButton(
            onClick = button.onClick,
            modifier = Modifier
                .padding(dimensions.tiny),
        ) {
            Icon(
                painter = button.icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(20.dp),
                tint = if (JewelTheme.isDark) {
                    Color.White
                } else {
                    Color.Black
                }
            )
        }
    }
}

data class Button(
    val icon: Painter,
    val onClick: () -> Unit
)