/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
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
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportScreen(
    throwable: Throwable,
    modifier: Modifier = Modifier
) = Scaffold(
    modifier = modifier,
    bottomBar = {
        BottomButtons(
            throwable = throwable,
            modifier = Modifier
                .padding(bottom = dimensions.default)
                .fillMaxWidth()
        )
    }
) { contentPadding ->
    Column(
        modifier = Modifier
            .padding(contentPadding)
            .padding(dimensions.default)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = dimensions.small,
            alignment = Alignment.CenterVertically
        )
    ) {
        Icon(
            Icons.Rounded.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = stringResource(Res.string.fatal_error_subtitle),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = stringResource(Res.string.fatal_error_message),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BottomButtons(
    throwable: Throwable,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.SpaceAround
) {

    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current

    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            delay(timeMillis = 1_000)
            copied = false
        }
    }

    Button(
        onClick = {
            clipboard.setText(
                AnnotatedString(
                    throwable.stackTraceToString()
                )
            )
            copied = true
        },
        enabled = !copied,
        modifier = Modifier.width(120.dp)
    ) {
        AnimatedContent(
            targetState = copied,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            }
        ) { copied ->
            if (copied) {
                Text(stringResource(Res.string.fatal_error_copied_label))
            } else {
                Text(stringResource(Res.string.fatal_error_copy_btn))
            }
        }
    }

    Button(
        onClick = {
            uriHandler.openUri(
                uri = "https://github.com/NeoUtils/NeoRegex/issues/new?template=bug_report.md"
            )
        },
    ) {
        Text(stringResource(Res.string.fatal_error_report_btn))
    }
}
