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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.model.Opened
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.common_confirm_btn
import com.neoutils.neoregex.core.resources.salvage_edit_name_dialog_title
import com.neoutils.neoregex.core.sharedui.event.SalvageAction
import org.jetbrains.compose.resources.stringResource

enum class Mode {
    EXPANDED,
    COMPACT;

    companion object {
        val Default = when (platform) {
            is Platform.Desktop -> COMPACT
            Platform.Android -> EXPANDED
            Platform.Web -> TODO("Not Supported")
        }
    }
}

@Composable
fun Salvage(
    opened: Opened,
    mode: Mode = Mode.Default,
    modifier: Modifier = Modifier,
    onAction: (SalvageAction) -> Unit = {},
    textStyle: TextStyle = TextStyle()
) = Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
) {

    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null,
    ).merge(textStyle)

    var showChangeName by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = opened.name,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        modifier = Modifier
            .padding(horizontal = dimensions.nano.m)
            .weight(
                weight = 1f,
                fill = mode == Mode.EXPANDED
            )
    ) { name ->
        Text(
            text = name,
            style = mergedTextStyle,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    Row {
        Icon(
            imageVector = Icons.TwoTone.Edit,
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { showChangeName = true }
                .minimalButton()
        )

        Icon(
            imageVector = Icons.TwoTone.Sync,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (opened.updated) 0.5f else 1f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(!opened.updated) {
                    onAction(SalvageAction.Reset)
                }
                .minimalButton()
        )

        Icon(
            imageVector = Icons.TwoTone.Save,
            contentDescription = null,
            tint = colorScheme.onSurface.copy(
                alpha = if (opened.canUpdate) 1f else 0.5f
            ),
            modifier = Modifier
                .clip(CircleShape)
                .clickable(opened.canUpdate) {
                    onAction(SalvageAction.Update)
                }
                .minimalButton()
        )

        Icon(
            imageVector = Icons.TwoTone.Close,
            contentDescription = null,
            tint = colorScheme.onSurface,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onAction(SalvageAction.Close) }
                .minimalButton()
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
                    style = typography.titleMedium.copy(
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