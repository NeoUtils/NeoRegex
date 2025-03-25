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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.History
import androidx.compose.material.icons.twotone.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.common.model.Salvage
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.buttons
import com.neoutils.neoregex.core.designsystem.theme.configButton

sealed class SalvageAction {
    data object Update : SalvageAction()
    data object Reset : SalvageAction()
    data class ChangeName(val name: String) : SalvageAction()
    data object Close : SalvageAction()
}

@Composable
fun SalvageUi(
    salvage: Salvage,
    modifier: Modifier = Modifier,
    onAction: (SalvageAction) -> Unit = {},
    textStyle: TextStyle = TextStyle()
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier.background(
        color = colorScheme.onSurface.copy(
            alpha = 0.05f
        ).compositeOver(
            colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(4.dp)
    )
) {

    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null,
    ).merge(textStyle)

    var showChangeName by remember { mutableStateOf(false) }

    Text(
        text = salvage.name,
        style = mergedTextStyle,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = Modifier
            .weight(weight = 1f, fill = false)
            .padding(start = 8.dp)
    )

    Row(
        modifier = Modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.TwoTone.Edit,
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { showChangeName = true }
                .configButton(buttons.small)
        )

        Icon(
            imageVector = Icons.TwoTone.History,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (salvage.updated) 0.5f else 1f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(!salvage.updated) { onAction(SalvageAction.Reset) }
                .configButton(buttons.small)
        )


        Icon(
            imageVector = Icons.TwoTone.Save,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (salvage.updated) 0.5f else 1f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(!salvage.updated) { onAction(SalvageAction.Update) }
                .configButton(buttons.small)
        )

        Icon(
            imageVector = Icons.TwoTone.Close,
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onAction(SalvageAction.Close) }
                .configButton(buttons.small)
        )
    }

    if (showChangeName) {
        PatternNameDialog(
            name = remember { mutableStateOf(salvage.name) },
            onDismissRequest = {
                showChangeName = false
            },
            title = {
                Text(
                    text = "Edit name",
                    style = typography.titleSmall.copy(
                        fontFamily = null,
                    )
                )
            },
            onConfirm = {
                onAction(SalvageAction.ChangeName(it))
            },
            confirmLabel = {
                Text(text = "Confirm")
            }
        )
    }
}