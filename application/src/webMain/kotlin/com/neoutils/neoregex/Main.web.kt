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

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.neoutils.neoregex.core.common.di.commonModule
import com.neoutils.neoregex.core.common.util.SizeManager
import com.neoutils.neoregex.core.crashreport.CrashReportHelper
import com.neoutils.neoregex.core.crashreport.di.crashReportModule
import com.neoutils.neoregex.core.database.di.databaseModule
import com.neoutils.neoregex.core.datasource.di.dataSourceModule
import com.neoutils.neoregex.core.manager.di.managerModule
import com.neoutils.neoregex.core.repository.di.repositoryModule
import com.neoutils.neoregex.feature.matcher.di.matcherModule
import com.neoutils.neoregex.feature.validator.di.validatorModule
import kotlinx.coroutines.flow.first
import org.jetbrains.skiko.wasm.onWasmReady
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {

    startKoin {
        modules(
            commonModule,
            managerModule,
            dataSourceModule,
            databaseModule,
            crashReportModule,
            repositoryModule,
            matcherModule,
            validatorModule,
        )
    }

    CrashReportHelper.service.setup()

    val sizeManager = SizeManager().apply { resize() }

    onWasmReady {
        CanvasBasedWindow(
            canvasElementId = "viewport-container",
            applyDefaultStyles = false,
            requestResize = {
                sizeManager.changes.first()
            }
        ) {
            WebApp()
        }
    }
}

