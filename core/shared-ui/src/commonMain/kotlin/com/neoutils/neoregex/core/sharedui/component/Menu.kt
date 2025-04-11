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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    salvageManager: SalvageManager = koinInject()
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
) {
    val canPopBack by navigation.canPopBack.collectAsStateWithLifecycle()

    val canSave by salvageManager.canSave.collectAsStateWithLifecycle(initialValue = false)

    var expanded by remember { mutableStateOf(false) }

    var showSavePatternDialog by remember { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    AnimatedContent(
        targetState = canPopBack,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
    ) { showBackButton ->
        if (showBackButton) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .aspectRatio(ratio = 1f)
                    .clickable(
                        onClick = {
                            coroutine.launch {
                                navigation.emit(Navigation.Event.OnBack)
                            }
                        }
                    ).padding(dimensions.tiny)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .aspectRatio(ratio = 1f)
                    .clickable { expanded = true }
                    .padding(dimensions.tiny)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.menu_new_btn)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.medium)
                )
            },
            onClick = {
                coroutine.launch {
                    salvageManager.close()
                }
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.menu_open_btn)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.medium)
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Saved
                        )
                    )
                }
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.menu_save_btn)) },
            enabled = canSave,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.medium)
                )
            },
            onClick = {
                showSavePatternDialog = true
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = stringResource(Res.string.menu_about_btn)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(dimensions.medium)
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.About
                        )
                    )
                }

                expanded = false
            }
        )
    }

    if (showSavePatternDialog) {
        PatternNameDialog(
            onDismissRequest = {
                showSavePatternDialog = false
            },
            onConfirm = {
                coroutine.launch {
                    salvageManager.save(it)
                }
            },
            confirmLabel = {
                Text(text = stringResource(Res.string.salvage_save_dialog_save_btn))
            },
            title = {
                Text(
                    text = stringResource(Res.string.salvage_save_dialog_title),
                    color = colorScheme.onSurfaceVariant,
                    style = typography.titleSmall.copy(
                        fontFamily = null,
                    )
                )
            }
        )
    }
}
