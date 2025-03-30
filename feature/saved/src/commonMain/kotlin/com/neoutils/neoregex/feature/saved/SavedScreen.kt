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

package com.neoutils.neoregex.feature.saved

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.neoutils.highlight.compose.remember.rememberAnnotatedString
import com.neoutils.neoregex.core.common.util.Syntax
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.buttons
import com.neoutils.neoregex.core.designsystem.theme.configButton
import com.neoutils.neoregex.core.sharedui.component.PatternNameDialog
import com.neoutils.neoregex.feature.saved.state.SavedUiState

class SavedScreen : Screen {

    @Composable
    override fun Content() = Box(
        modifier = Modifier
            .background(colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val viewModel = koinScreenModel<SavedViewModel>()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        var showChangeTitle by remember { mutableStateOf<SavedUiState.Pattern?>(null) }

        if (uiState.patterns.isEmpty()) {
            Text(text = "nothing saved yet")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                ),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.patterns) { pattern ->
                    Pattern(
                        pattern = pattern,
                        onOpen = {
                            viewModel.open(pattern.id)
                        },
                        onDelete = {
                            viewModel.delete(pattern.id)
                        },
                        onEdit = {
                            showChangeTitle = pattern
                        }
                    )
                }
            }
        }

        showChangeTitle?.let { pattern ->
            PatternNameDialog(
                name = remember { mutableStateOf(pattern.title) },
                onDismissRequest = {
                    showChangeTitle = null
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
                    viewModel.changeTitle(pattern.id, it)
                },
                confirmLabel = {
                    Text(text = "Confirm")
                }
            )
        }
    }
}

@Composable
private fun Pattern(
    pattern: SavedUiState.Pattern,
    modifier: Modifier = Modifier,
    onOpen: () -> Unit = {},
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    syntax: Syntax.Regex = remember { Syntax.Regex() }
) = Surface(
    modifier = modifier,
    shape = RoundedCornerShape(4.dp),
    color = colorScheme.surfaceContainer,
    contentColor = colorScheme.onSurface,
    border = BorderStroke(
        width = 1.dp,
        color = colorScheme.outlineVariant
    ),
) {

    val clipboardManager = LocalClipboardManager.current

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 4.dp
            ),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = pattern.title,
                style = typography.titleSmall,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp)
            )

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onEdit)
                    .configButton(
                        config = buttons.small
                    )
            )

            Spacer(Modifier.weight(weight = 1f))

            IconButton(
                onClick = onOpen,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.padding(4.2.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        HorizontalDivider()

        val interactionSource = remember { MutableInteractionSource() }
        val interaction by interactionSource.interactions.collectAsState(initial = null)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { clipboardManager.setText(AnnotatedString(pattern.text)) }
                .hoverable(interactionSource)
        ) {
            Text(
                text = syntax.highlight.rememberAnnotatedString(pattern.text),
                style = typography.bodyLarge,
                overflow = TextOverflow.Visible,
                maxLines = 1,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            )

            when (interaction) {
                is HoverInteraction.Enter,
                is PressInteraction.Press,
                is PressInteraction.Release -> {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(16.dp)
                            .size(20.dp)
                    )
                }
            }
        }
    }
}

