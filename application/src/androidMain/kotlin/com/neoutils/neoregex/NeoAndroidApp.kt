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
import com.neoutils.neoregex.feature.validator.di.validatorModule
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.dispatcher.di.navigationModule
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import org.koin.core.context.GlobalContext.startKoin

class NeoAndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                dataSourceModule,
                repositoryModule,
                navigationModule,
                matcherModule,
                validatorModule
            )
        }
    }
}