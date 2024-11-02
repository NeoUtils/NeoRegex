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

package com.neoutils.neoregex

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.application
import com.neoutils.neoregex.core.common.util.UiMode
import com.neoutils.neoregex.core.common.util.isDark
import com.neoutils.neoregex.core.common.util.resolve
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import com.neoutils.neoregex.core.resources.github
import com.neoutils.neoregex.core.sharedui.component.DefaultHeader
import com.neoutils.neoregex.core.sharedui.component.NeoWindow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {

    val uiMode = remember { UiMode.resolve() }

    NeoTheme(uiMode.isDark) {
        NeoWindow(
            header = {
                TitleBar()
            }
        ) {
            App()
        }
    }
}

@Composable
private fun FrameWindowScope.TitleBar(
    title: String = stringResource(Res.string.app_name),
    uiMode: UiMode = remember { UiMode.resolve() }
) {
    DefaultHeader(
        title = title,
        options = {
            val uriHandler = LocalUriHandler.current

            Icon(
                painter = painterResource(Res.drawable.github),
                contentDescription = null,
                tint = when (uiMode) {
                    UiMode.LIGHT -> colorScheme.onSurface
                    UiMode.DARK -> colorScheme.onSurface
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = {
                            uriHandler.openUri(
                                uri = "https://github.com/NeoUtils/NeoRegex"
                            )
                        }
                    )
                    .padding(dimensions.medium)
                    .aspectRatio(ratio = 1f)
            )
        }
    )
}
