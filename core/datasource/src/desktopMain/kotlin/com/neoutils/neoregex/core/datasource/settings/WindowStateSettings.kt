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

package com.neoutils.neoregex.core.datasource.settings

import com.neoutils.neoregex.core.datasource.WindowStateDataSource
import com.neoutils.neoregex.core.datasource.model.WindowStateData
import com.neoutils.neoregex.core.datasource.model.WindowStateData.Size
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.prefs.Preferences

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
internal class WindowStateSettings(
    private val settings: ObservableSettings = PreferencesSettings(Preferences.userRoot())
) : WindowStateDataSource {

    private val _flow = MutableStateFlow(
        settings.decodeValue(
            serializer = WindowStateData.serializer(),
            defaultValue = WindowStateData.Default,
            key = KEY
        )
    )

    override val flow = _flow.asStateFlow()
    override val current get() = flow.value

    override fun update(block: (WindowStateData) -> WindowStateData): WindowStateData {

        val windowState = block(flow.value)

        settings.encodeValue(WindowStateData.serializer(), KEY, windowState)
        _flow.value = windowState.copy(
            size = Size(
                width = 800,
                height = 600
            )
        )

        return windowState
    }

    companion object {
        const val KEY = "window_state"
    }
}