/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.ic_redo_24
import com.neoutils.neoregex.core.resources.ic_undo_24
import com.neoutils.neoregex.core.sharedui.extension.onLongPress
import org.jetbrains.compose.resources.painterResource

@Composable
fun History(
    history: HistoryState,
    onAction: (FooterAction.History) -> Unit,
    modifier: Modifier = Modifier,
    shape: CornerBasedShape = RoundedCornerShape(dimensions.small.s)
) = Row(
    modifier = modifier
        .height(IntrinsicSize.Min)
        .border(
            width = 1.dp,
            color = colorScheme.outline,
            shape = RoundedCornerShape(dimensions.small.s)
        )
) {

    val contentColor = LocalContentColor.current

    Icon(
        painter = painterResource(Res.drawable.ic_undo_24),
        contentDescription = null,
        tint = contentColor.copy(
            alpha = if (history.canUndo) 1f else 0.5f
        ),
        modifier = Modifier
            .clip(
                shape.copy(
                    topEnd = CornerSize(0.dp),
                    bottomEnd = CornerSize(0.dp)
                )
            )
            .clickable(history.canUndo) {
                onAction(
                    FooterAction.History.Undo()
                )
            }
            .onLongPress {
                onAction(
                    FooterAction.History.Undo()
                )
            }
            .padding(
                vertical = dimensions.nano.m,
                horizontal = dimensions.small.s,
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
        tint = contentColor.copy(
            alpha = if (history.canRedo) 1f else 0.5f
        ),
        modifier = Modifier
            .clip(
                shape.copy(
                    topStart = CornerSize(0.dp),
                    bottomStart = CornerSize(0.dp)
                )
            )
            .clickable(history.canRedo) {
                onAction(
                    FooterAction.History.Redo()
                )
            }
            .onLongPress {
                onAction(
                    FooterAction.History.Redo()
                )
            }
            .padding(
                vertical = dimensions.nano.m,
                horizontal = dimensions.small.s,
            )
    )
}