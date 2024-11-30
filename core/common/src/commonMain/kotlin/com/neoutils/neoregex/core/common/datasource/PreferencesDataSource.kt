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

package com.neoutils.neoregex.core.common.datasource

import androidx.compose.ui.Alignment
import com.neoutils.neoregex.core.common.model.Preferences
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface PreferencesDataSource {

    val preferences: StateFlow<Preferences>

    fun update(block: (Preferences) -> Preferences): Preferences
}

class PreferencesDataSourceImpl(
    private val settings: Settings = Settings()
) : PreferencesDataSource {

    private val _preferences = MutableStateFlow(settings.toPreferences())
    override val preferences = _preferences.asStateFlow()

    override fun update(
        block: (Preferences) -> Preferences
    ): Preferences {

        val preferences = block(preferences.value)

        settings.apply(preferences)
        _preferences.value = preferences

        return preferences
    }
}

sealed class PreferencesTokens<T>(
    val key: String,
    val pairs: Map<String, T>
) {
    data object InfosAlignment : PreferencesTokens<Alignment>(
        key = "MATCHES_INFOS_ALIGNMENT",
        pairs = mapOf(
            "BOTTOM_END" to Alignment.BottomEnd,
            "TOP_END" to Alignment.TopEnd
        )
    )

    fun get(key: String?): T {

        val pair = pairs.entries.find {
            it.key == key
        } ?: pairs.entries.first()

        return pair.value
    }

    fun keyOf(value: T?): String {

        val pair = pairs.entries.find {
            it.value == value
        } ?: pairs.entries.first()

        return pair.key
    }
}

private fun Settings.toPreferences(): Preferences {

    return Preferences(
        matchesInfosAlignment = PreferencesTokens.InfosAlignment.get(
            get<String>(PreferencesTokens.InfosAlignment.key)
        )
    )
}

private fun Settings.apply(preferences: Preferences) {
    set(
        PreferencesTokens.InfosAlignment.key,
        PreferencesTokens.InfosAlignment.keyOf(preferences.matchesInfosAlignment)
    )
}