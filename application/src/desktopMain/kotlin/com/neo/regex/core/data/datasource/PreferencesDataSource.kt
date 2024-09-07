package com.neo.regex.core.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neo.regex.core.domain.model.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import androidx.datastore.preferences.core.Preferences as DataStorePreferences

class PreferencesDataSource(
    private val datastore: DataStore<DataStorePreferences>,
) {

    private val uiModeKey = stringPreferencesKey(name = "uiMode")

    val preferencesFlow = datastore.data.map {
        Preferences(
            uiMode = Preferences.UiMode.get(
                key = it[uiModeKey] ?: Preferences.Default.uiMode.key
            )
        )
    }

    val preferences: Preferences
        get() = runBlocking {
            preferencesFlow.first()
        }

    suspend fun update(
        block: (Preferences) -> Preferences
    ): Preferences {

        val newPreferences = block(preferencesFlow.first())

        datastore.edit {
            it[uiModeKey] = newPreferences.uiMode.key
        }

        return newPreferences
    }
}