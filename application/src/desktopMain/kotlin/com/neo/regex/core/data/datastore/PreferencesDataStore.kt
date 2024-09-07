package com.neo.regex.core.data.datastore

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import java.io.File

val dataStore = PreferenceDataStoreFactory.createWithPath(
    produceFile = {
        File("NeoRegex/datastore/preferences.preferences_pb").path.toPath()
    },
)