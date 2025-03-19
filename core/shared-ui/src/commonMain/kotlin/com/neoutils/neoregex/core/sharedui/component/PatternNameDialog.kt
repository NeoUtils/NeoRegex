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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternNameDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    name: MutableState<String> =  remember { mutableStateOf("") },
    hint: @Composable () -> Unit = {
        Text(
            text = "Pattern name",
            style = LocalTextStyle.current.let {
                it.copy(
                    color = it.color.copy(
                        alpha = 0.5f
                    )
                )
            },
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    },
    title: @Composable () -> Unit = {
        Text(
            text = "Save pattern",
            color = colorScheme.onSurfaceVariant,
            style = typography.titleSmall.copy(
                fontFamily = null,
            )
        )
    },
    cancelLabel: @Composable () -> Unit = {
        Text(text = "Cancel")
    },
    confirmLabel: @Composable () -> Unit = {
        Text(text = "Save")
    }
) = BasicAlertDialog(
    onDismissRequest = onDismissRequest,
    modifier = modifier
) {
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
                title()
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
                    hint = hint,
                    value = name.value,
                    onValueChange = { name.value = it },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onConfirm(name.value)
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
                        cancelLabel()
                    }

                    OutlinedButton(
                        onClick = {
                            onConfirm(name.value)
                            onDismissRequest()
                        },
                        enabled = name.value.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onBackground
                        )
                    ) {
                        confirmLabel()
                    }
                }
            }
        }
    }
}
