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

@file:OptIn(ExperimentalResourceApi::class)

package com.neoutils.neoregex.feature.about.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.screen.Screen
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.rememberLibraries
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

class LibrariesScreen : Screen {

    @Composable
    override fun Content() {
        val libraries by rememberLibraries {
            Res.readBytes("files/aboutlibraries.json").decodeToString()
        }

        val uriHandler = LocalUriHandler.current

        LibrariesContainer(
            libraries = libraries,
            modifier = Modifier.fillMaxSize(),
            onLibraryClick = { library ->
                library.scm?.url?.let {
                    uriHandler.openUri(it)
                }
            },
            showLicenseBadges = platform != Platform.Web
        )
    }
}
