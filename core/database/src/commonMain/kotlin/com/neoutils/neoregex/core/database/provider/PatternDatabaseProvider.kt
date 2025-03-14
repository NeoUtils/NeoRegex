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

package com.neoutils.neoregex.core.database.provider

import com.neoutils.neoregex.core.database.db.PatternDatabase
import com.neoutils.neoregex.core.database.factory.DriverFactory

internal class PatternDatabaseProvider(
    private val driverFactory: DriverFactory,
) {

    val database by lazy { createDatabase() }

    private fun createDatabase(): PatternDatabase {

        val database = PatternDatabase(
            driver = driverFactory.createDriver(
                databaseName = DATABASE_NAME
            )
        )

        return database
    }

    private companion object {
        private const val DATABASE_NAME = "neoregex-db"
    }
}
