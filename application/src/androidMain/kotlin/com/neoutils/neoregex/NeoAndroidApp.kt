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

package com.neoutils.neoregex

import android.app.Application
import android.content.Context
import com.neoutils.neoregex.core.database.di.databaseModule
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.dispatcher.di.dispatcherModule
import com.neoutils.neoregex.core.manager.di.managerModule
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.di.appModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import com.neoutils.neoregex.feature.validator.di.validatorModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

class NeoAndroidApp : Application() {

    private val module = module {
        single<Context> { applicationContext }
    }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                module,
                managerModule,
                dataSourceModule,
                databaseModule,
                repositoryModule,
                dispatcherModule,
                matcherModule,
                validatorModule,
                appModule
            )
        }
    }
}