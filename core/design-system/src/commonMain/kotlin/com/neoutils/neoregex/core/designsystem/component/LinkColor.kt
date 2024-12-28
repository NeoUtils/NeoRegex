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

package com.neoutils.neoregex.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import com.neoutils.neoregex.core.designsystem.theme.Blue500
import com.neoutils.neoregex.core.designsystem.theme.Blue600
import com.neoutils.neoregex.core.designsystem.theme.Purple500

data class LinkColor(
    val idle: Color = Blue500,
    val hover: Color = Blue600,
    val press: Color = Purple500,
    val pressed: Color = press
)

enum class LinkPress {
    IDLE,
    PRESS,
    PRESSED
}

enum class LinkHover {
    IDLE,
    HOVER
}

@Composable
fun Link(
    text: String,
    onClick: () -> Unit = {},
    colors: LinkColor = LinkColor(),
    enabled: Boolean = true,
    style: TextStyle = typography.labelMedium,
    modifier: Modifier = Modifier
) {

    val interactionSource = remember { MutableInteractionSource() }

    var press by remember { mutableStateOf(LinkPress.IDLE) }
    var hover by remember { mutableStateOf(LinkHover.IDLE) }

    LaunchedEffect(Unit) {
        interactionSource.interactions.collect {
            when (it) {
                is HoverInteraction.Enter -> {
                    hover = LinkHover.HOVER
                }

                is HoverInteraction.Exit -> {
                    hover = LinkHover.IDLE
                }

                is PressInteraction.Press -> {
                    press = LinkPress.PRESS
                }

                is PressInteraction.Release,
                is PressInteraction.Cancel -> {
                    press = LinkPress.PRESSED
                }
            }
        }
    }

    val mergedTextStyle = TextStyle(
        textDecoration = when (hover) {
            LinkHover.IDLE -> null
            LinkHover.HOVER -> TextDecoration.Underline
        }
    ).merge(style)

    return Text(
        text = text,
        modifier = modifier.clickable(
            onClick = onClick,
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled
        ),
        style = mergedTextStyle.copy(
            color = remember(press, hover, colors) {
                when {
                    press == LinkPress.PRESS -> colors.press
                    hover == LinkHover.HOVER -> colors.hover
                    press == LinkPress.PRESSED -> colors.pressed
                    else -> colors.idle
                }
            },
        )
    )
}