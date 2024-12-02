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

import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
internal class PreferencesSettings(
    private val settings: Settings = Settings()
) : PreferencesDataSource {

    private val _flow = MutableStateFlow(
        settings.decodeValue(
            serializer = Preferences.serializer(),
            defaultValue = Preferences.Default,
            key = KEY,
        )
    )

    override val flow = _flow.asStateFlow()
    override val current get() = flow.value

    override fun update(
        block: (Preferences) -> Preferences
    ): Preferences {

        val preferences = block(flow.value)

        settings.encodeValue(Preferences.serializer(), KEY, preferences)
        _flow.value = preferences

        return preferences
    }

    companion object {
        private const val KEY = "preferences"
    }
}