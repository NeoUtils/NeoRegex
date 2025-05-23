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

package com.neoutils.neoregex.core.database.di

import com.neoutils.neoregex.core.database.PatternsSqlDelightDataSource
import com.neoutils.neoregex.core.database.factory.DriverFactory
import com.neoutils.neoregex.core.database.factory.PatternWebDriverFactory
import com.neoutils.neoregex.core.database.provider.PatternDatabaseProvider
import com.neoutils.neoregex.core.datasource.PatternsDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val databaseModule = module {
    single { PatternWebDriverFactory() } bind DriverFactory::class
    singleOf(::PatternsSqlDelightDataSource) bind PatternsDataSource::class
    single { PatternDatabaseProvider(get()).database }
}
