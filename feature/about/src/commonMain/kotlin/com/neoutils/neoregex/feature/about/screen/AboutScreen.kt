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

package com.neoutils.neoregex.feature.about.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.neoutils.neoregex.NeoConfig.code
import com.neoutils.neoregex.NeoConfig.version
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.designsystem.component.Link
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.about_description_text
import com.neoutils.neoregex.core.resources.about_version_text
import com.neoutils.neoregex.core.resources.ic_launcher
import com.neoutils.neoregex.feature.about.component.RuntimeInfos
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

class AboutScreen : Screen {

    @Composable
    override fun Content() = Column(
        modifier = Modifier
            .padding(dimensions.default)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            dimensions.small,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProvideTextStyle(typography.labelMedium) {
            Image(
                painter = painterResource(Res.drawable.ic_launcher),
                modifier = Modifier.size(56.dp),
                contentDescription = null
            )

            Text(
                stringResource(
                    Res.string.about_version_text,
                    version,
                    code
                ),
            )

            Spacer(Modifier.height(dimensions.small))

            Column(
                modifier = Modifier.sizeIn(
                    maxWidth = 450.dp
                ),
                verticalArrangement = Arrangement.spacedBy(
                    dimensions.small,
                    Alignment.CenterVertically
                )
            ) {

                Text(stringResource(Res.string.about_description_text))

                if (platform is Platform.Desktop) {
                    RuntimeInfos()
                }

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = dimensions.tiny
                    )
                )

                val navigator = LocalNavigator.current

                Link(
                    text = "Open source libraries",
                    onClick = {
                        navigator?.push(LibrariesScreen())
                    }
                )

                val uriHandler = LocalUriHandler.current

                Link(
                    text = "Source code",
                    onClick = {
                        uriHandler.openUri(uri = "https://github.com/NeoUtils/NeoRegex")
                    }
                )

                Link(
                    text = "License",
                    onClick = {
                        uriHandler.openUri(uri = "https://github.com/NeoUtils/NeoRegex#GPL-3.0-1-ov-file")
                    }
                )
            }
        }
    }
}