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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.neoutils.highlight.compose.remember.rememberAnnotatedString
import com.neoutils.neoregex.core.common.extension.withSpanStyles
import com.neoutils.neoregex.core.common.util.Syntax
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.fontSizes
import com.neoutils.neoregex.core.resources.*
import com.neoutils.neoregex.core.sharedui.component.NeoRegexDialog
import com.neoutils.neoregex.core.sharedui.component.PatternNameDialog
import com.neoutils.neoregex.feature.saved.state.SavedUiState
import org.jetbrains.compose.resources.stringResource

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
        var showDeletePattern by remember { mutableStateOf<SavedUiState.Pattern?>(null) }

        if (uiState.patterns.isEmpty()) {
            Text(text = stringResource(Res.string.saved_empty))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = dimensions.default,
                ),
                contentPadding = PaddingValues(dimensions.default)
            ) {
                items(uiState.patterns) { pattern ->
                    Pattern(
                        pattern = pattern,
                        onOpen = {
                            viewModel.open(pattern.id)
                        },
                        onDelete = {
                            showDeletePattern = pattern
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
                        text = stringResource(Res.string.salvage_edit_name_dialog_title),
                        style = typography.titleSmall.copy(
                            fontFamily = null,
                        )
                    )
                },
                onConfirm = {
                    viewModel.changeTitle(pattern.id, it)
                },
                confirmLabel = {
                    Text(text = stringResource(Res.string.common_confirm_btn))
                },
            )
        }

        showDeletePattern?.let { pattern ->
            NeoRegexDialog(
                onDismissRequest = {
                    showDeletePattern = null
                },
                onConfirm = {
                    viewModel.delete(pattern.id)
                },
                title = {
                    Text(
                        text = stringResource(Res.string.saved_delete_pattern_title),
                        color = colorScheme.onSurfaceVariant,
                        style = typography.titleSmall.copy(
                            fontFamily = null,
                        )
                    )
                },
            ) {
                Text(
                    text = stringResource(Res.string.saved_description, pattern.title).let {

                        val startIndex = it.indexOf(pattern.title)

                        it.withSpanStyles(
                            spanStyles = listOf(
                                AnnotatedString.Range(
                                    item = SpanStyle(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    start = startIndex,
                                    end = startIndex + pattern.title.length
                                )
                            )
                        )
                    },
                    color = colorScheme.onSurfaceVariant,
                    style = typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
    Column {

        val clipboardManager = LocalClipboardManager.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = dimensions.tiny
            ),
            modifier = Modifier
                .padding(dimensions.tiny)
                .fillMaxWidth(),
        ) {
            Text(
                text = pattern.title,
                style = typography.titleSmall,
                fontSize = fontSizes.small,
                modifier = Modifier.padding(start = dimensions.short)
            )

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onEdit)
                    .size(dimensions.large)
                    .padding(dimensions.tiny)
            )

            Spacer(Modifier.weight(weight = 1f))

            IconButton(
                onClick = onOpen,
                enabled = !pattern.opened,
                modifier = Modifier.size(dimensions.large)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.padding(4.2.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(dimensions.large)
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
                    .padding(dimensions.default)
            )

            val interaction by interactionSource.interactions.collectAsStateWithLifecycle(initialValue = null)

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
