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
import com.neoutils.neoregex.core.common.di.commonModule
import com.neoutils.neoregex.core.crashreport.CrashReportHelper
import com.neoutils.neoregex.core.crashreport.di.crashReportModule
import com.neoutils.neoregex.core.database.di.databaseModule
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.manager.di.managerModule
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import com.neoutils.neoregex.feature.saved.di.savedModule
import com.neoutils.neoregex.feature.validator.di.validatorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class NeoAndroidApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@NeoAndroidApp)
            modules(
                commonModule,
                managerModule,
                dataSourceModule,
                databaseModule,
                repositoryModule,
                matcherModule,
                validatorModule,
                savedModule,
                crashReportModule
            )
        }

        CrashReportHelper.service.setup()
    }
}