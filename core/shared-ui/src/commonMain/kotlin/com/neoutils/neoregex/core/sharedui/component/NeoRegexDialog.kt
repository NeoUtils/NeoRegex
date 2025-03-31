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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeoRegexDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    enableConfirm: Boolean = true,
    cancelLabel: @Composable () -> Unit = {
        Text(text = "Cancel")
    },
    confirmLabel: @Composable () -> Unit = {
        Text(text = "Delete")
    },
    content: @Composable () -> Unit
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

                content()

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
                            onConfirm()
                            onDismissRequest()
                        },
                        enabled = enableConfirm,
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
