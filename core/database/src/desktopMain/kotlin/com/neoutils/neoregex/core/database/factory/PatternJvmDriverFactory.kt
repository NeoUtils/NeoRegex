/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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

package com.neoutils.neoregex.core.database.factory

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

internal class PatternJvmDriverFactory(
    private val schema: SqlSchema<QueryResult.Value<Unit>>
) : DriverFactory {
    override fun createDriver(databaseName: String): SqlDriver {

        val driver = runCatching {
            JdbcSqliteDriver(
                url = "jdbc:sqlite:$databaseName.db",
                schema = schema,
            )
        }.getOrElse {
            fixDatabaseVersion(databaseName)
        }

        return driver
    }

    /**
     * Fixes the database version created incorrectly in version 3.0.0.
     *
     * In version 3.0.0, a configuration error generated databases with version 0 instead of 1.
     * Every database should start at version 1 and version 0 indicates that the database has not been created yet.
     *
     * @param databaseName Name of the database to be fixed
     * @return [SqlDriver] configured with the correct version
     */
    private fun fixDatabaseVersion(
        databaseName: String
    ): SqlDriver {
        val driver = JdbcSqliteDriver(
            url = "jdbc:sqlite:$databaseName.db",
        )

        schema.migrate(
            driver = driver,
            oldVersion = 0,
            newVersion = schema.version
        )

        driver.setVersion(schema.version)

        return driver
    }

    private fun JdbcSqliteDriver.setVersion(version: Long): Long {
        return execute(
            identifier = null,
            sql = "PRAGMA user_version = $version",
            parameters = 0,
            binders = null
        ).value
    }
}
