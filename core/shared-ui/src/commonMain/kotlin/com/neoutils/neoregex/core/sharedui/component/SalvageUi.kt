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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.Edit
import androidx.compose.material.icons.twotone.Save
import androidx.compose.material.icons.twotone.Sync
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
import com.neoutils.neoregex.core.common.model.Opened
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.buttons
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.configButton
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.common_confirm_btn
import com.neoutils.neoregex.core.resources.salvage_edit_name_dialog_title
import org.jetbrains.compose.resources.stringResource

sealed class SalvageAction {
    data object Update : SalvageAction()
    data object Reset : SalvageAction()
    data class ChangeName(val name: String) : SalvageAction()
    data object Close : SalvageAction()
}

@Composable
fun SalvageUi(
    opened: Opened,
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

    AnimatedContent(
        targetState = opened.name,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { name ->
        Text(
            text = name,
            style = mergedTextStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .weight(weight = 1f, fill = false)
                .padding(start = dimensions.small)
        )
    }

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
            imageVector = Icons.TwoTone.Sync,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (opened.updated) 0.5f else 1f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    enabled = !opened.updated,
                    onClick = {
                        onAction(SalvageAction.Reset)
                    }
                )
                .configButton(buttons.small)
        )


        Icon(
            imageVector = Icons.TwoTone.Save,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (opened.canUpdate) 1f else 0.5f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    enabled = opened.canUpdate,
                    onClick = {
                        onAction(SalvageAction.Update)
                    }
                )
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
            name = remember { mutableStateOf(opened.name) },
            onDismissRequest = {
                showChangeName = false
            },
            title = {
                Text(
                    text = stringResource(Res.string.salvage_edit_name_dialog_title),
                    style = typography.titleSmall.copy(
                        fontFamily = null,
                    )
                )
            },
            onConfirm = {
                onAction(SalvageAction.ChangeName(it))
            },
            confirmLabel = {
                Text(text = stringResource(Res.string.common_confirm_btn))
            },
        )
    }
}