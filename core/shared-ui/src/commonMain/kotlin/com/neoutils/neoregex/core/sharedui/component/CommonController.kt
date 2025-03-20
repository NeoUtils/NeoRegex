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

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.buttons
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.control.Controller
import com.neoutils.neoregex.core.dispatcher.event.Command
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun CommonController(
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(dimensions.tiny),
    verticalAlignment = Alignment.CenterVertically
) {
    Menu(
        modifier = Modifier.fillMaxHeight()
    )

    Navigation(
        modifier = Modifier.fillMaxHeight(),
    )
}

@Composable
private fun Menu(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    control: Controller = koinInject(),
    salvageManager: SalvageManager = koinInject()
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center,
) {
    val canPopBack by navigation.canPopBack.collectAsStateWithLifecycle()

    val salvage by salvageManager.salvage.collectAsStateWithLifecycle(initialValue = null)

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
                    .clickable(
                        onClick = {
                            coroutine.launch {
                                navigation.emit(Navigation.Event.OnBack)
                            }
                        }
                    )
                    .size(buttons.size)
                    .padding(buttons.padding)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { expanded = true }
                    .size(buttons.size)
                    .padding(buttons.padding)
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
        DropdownMenuItem(
            text = { Text(text = "New") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                coroutine.launch {
                    control.dispatcher(Command.New)
                    salvageManager.close()
                }
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = "Open") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                // TODO: implement
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = "Save") },
            enabled = salvage == null,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                showSavePatternDialog = true
                expanded = false
            }
        )

        DropdownMenuItem(
            text = { Text(text = "About") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.About
                        )
                    )
                    expanded = false
                }
            }
        )
    }

    if (showSavePatternDialog) {
        SavePatternDialog(
            onDismissRequest = {
                showSavePatternDialog = false
            },
            onSave = {
                coroutine.launch {
                    control.dispatcher(Command.Save(it))
                }
            }
        )
    }
}

@Composable
private fun Navigation(
    modifier: Modifier = Modifier,
    navigation: NavigationManager = koinInject(),
    textStyle: TextStyle = TextStyle()
) = Column(
    modifier = modifier,
    verticalArrangement = Arrangement.Center
) {
    val current by navigation.screen.collectAsStateWithLifecycle()

    val expanded = remember { mutableStateOf(false) }

    val coroutine = rememberCoroutineScope()

    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null
    ).merge(textStyle)

    val canSelectScreen = when (current) {
        Navigation.Screen.About -> false
        Navigation.Screen.Libraries -> false
        Navigation.Screen.Matcher -> true
        Navigation.Screen.Validator -> true
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(dimensions.tiny))
            .clickable(canSelectScreen) { expanded.value = true }
            .padding(dimensions.tiny),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Text(
            text = stringResource(current.title),
            style = mergedTextStyle
        )

        AnimatedVisibility(
            visible = canSelectScreen,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally(),
        ) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                modifier = Modifier.size(18.dp),
                contentDescription = null
            )
        }
    }

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(Navigation.Screen.Matcher.title),
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Matcher
                        )
                    )
                    expanded.value = false
                }
            },
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(Navigation.Screen.Validator.title),
                )
            },
            onClick = {
                coroutine.launch {
                    navigation.emit(
                        Navigation.Event.Navigate(
                            screen = Navigation.Screen.Validator
                        )
                    )
                    expanded.value = false
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavePatternDialog(
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) = BasicAlertDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier
) {

    var name by remember { mutableStateOf("") }

    Surface(
        color = colorScheme.background,
        contentColor = colorScheme.onBackground,
        border = BorderStroke(width = 1.dp, colorScheme.outline)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .background(colorScheme.surfaceVariant)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Save pattern",
                    color = colorScheme.onSurfaceVariant,
                    style = typography.titleSmall.copy(
                        fontFamily = null,
                    )
                )
            }

            HorizontalDivider(color = colorScheme.outlineVariant)

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var focused by remember { mutableStateOf(false) }

                val focusRequester = remember { FocusRequester() }

                LaunchedEffect(Unit) { focusRequester.requestFocus() }

                NeoTextField(
                    hint = "Pattern name",
                    value = name,
                    onValueChange = { name = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onSave(name)
                            onDismissRequest()
                        }
                    ),
                    singleLine = true,
                    textStyle = typography.bodyMedium.copy(
                        color = colorScheme.onBackground
                    ),
                    contentPadding = PaddingValues(dimensions.wide),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focused = it.isFocused }
                        .border(
                            width = 1.dp,
                            color = colorScheme.outline.copy(
                                alpha = if (focused) 1f else 0.5f
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedButton(
                        onClick = {
                            onDismissRequest()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onBackground
                        )
                    ) {
                        Text(text = "Cancel")
                    }

                    OutlinedButton(
                        onClick = {
                            onSave(name)
                            onDismissRequest()
                        },
                        enabled = name.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onBackground
                        )
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}
