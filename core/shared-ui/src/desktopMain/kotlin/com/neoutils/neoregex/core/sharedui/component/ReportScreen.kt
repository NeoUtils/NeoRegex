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

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReportScreen(
    throwable: Throwable,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier.padding(dimensions.default),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(
        space = dimensions.small,
        alignment = Alignment.Top
    )
) {

    Icon(
        Icons.Rounded.ErrorOutline,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        tint = MaterialTheme.colorScheme.error
    )

    Text(
        text = stringResource(Res.string.error_title),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium
    )

    Text(
        text = stringResource(Res.string.error_message),
        textAlign = TextAlign.Center,
    )

    Spacer(Modifier.weight(weight = 1f))

    val uriHandler = LocalUriHandler.current
    val clipboard = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Button(
            onClick = {
                clipboard.setText(
                    AnnotatedString(throwable.stackTraceToString())
                )
            },
        ) {
            Text(stringResource(Res.string.copy_error))
        }

        Button(
            onClick = {
                val title = "Crash:%20${throwable::class.java.name}"
                uriHandler.openUri("https://github.com/NeoUtils/NeoRegex/issues/new?title=$title")
            },
        ) {
            Text(stringResource(Res.string.report_error))
        }
    }
}
