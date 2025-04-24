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

package com.neoutils.neoregex.core.crashreport.impl

import android.content.Context
import com.bugsnag.android.Bugsnag
import com.bugsnag.android.Client
import com.bugsnag.android.performance.BugsnagPerformance
import com.neoutils.neoregex.BuildKonfig
import com.neoutils.neoregex.core.crashreport.CrashReportService

internal class CrashReportServiceImpl(
    private val context: Context
) : CrashReportService {

    private var client: Client? = null

    override fun setup() {
        client = BuildKonfig.BUGSNAG_API_KEY?.let { apiKey ->
            BugsnagPerformance.start(context, apiKey)
            Bugsnag.start(context, apiKey)
        }
    }

    override fun report(t: Throwable) {
        client?.notify(t)
    }
}