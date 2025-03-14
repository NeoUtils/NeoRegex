package com.neoutils.neoregex.core.database.factory

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.neoutils.neoregex.core.database.db.PatternDatabase

internal class PatternAndroidDriverFactory(
    private val context: Context,
) : DriverFactory {

    override fun createDriver(
        databaseName: String
    ): SqlDriver {
        return AndroidSqliteDriver(
            PatternDatabase.Schema,
            context,
            databaseName
        )
    }
}
