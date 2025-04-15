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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.designsystem.textfield.NeoTextField
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.salvage_pattern_name_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun PatternNameDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    modifier: Modifier = Modifier,
    name: MutableState<String> = remember { mutableStateOf("") },
    title: @Composable () -> Unit,
    confirmLabel: @Composable () -> Unit
) = NeoRegexDialog(
    onDismissRequest = onDismissRequest,
    title = title,
    confirmLabel = confirmLabel,
    onConfirm = {
        onConfirm(name.value)
    },
    enableConfirm = name.value.isNotBlank(),
    modifier = modifier
) {
    var focused by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    NeoTextField(
        value = name.value,
        onValueChange = { name.value = it },
        keyboardActions = KeyboardActions(
            onDone = {
                onConfirm(name.value)
                onDismissRequest()
            }
        ),
        singleLine = true,
        textStyle = typography.bodyMedium,
        contentPadding = PaddingValues(dimensions.small.x),
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
            ),
        hint = {
            Text(
                text = stringResource(Res.string.salvage_pattern_name_hint),
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
        }
    )
}
