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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Error
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.fatal_error_report_tab
import com.neoutils.neoregex.core.resources.fatal_error_stack_trace_tab
import com.neoutils.neoregex.core.resources.fatal_error_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.FatalErrorWindow(
    throwable: Throwable
) {
    val state = rememberDialogState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(500.dp, 400.dp)
    )

    DialogWindow(
        onCloseRequest = ::exitApplication,
        state = state,
        title = stringResource(
            Res.string.fatal_error_title,
            throwable::class.java.name
        ),
        icon = rememberVectorPainter(Icons.TwoTone.Error)
    ) {
        Column(Modifier.fillMaxSize()) {

            var current by remember { mutableStateOf(Tab.REPORT_ERROR) }

            val selectedTabIndex = remember(current) { Tab.entries.indexOf(current) }

            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                Tab.entries.forEach { tab ->
                    Tab(
                        text = { Text(stringResource(tab.title)) },
                        selected = current == tab,
                        onClick = {
                            current = tab
                        }
                    )
                }
            }

            when (current) {
                Tab.REPORT_ERROR -> {
                    ReportScreen(
                        throwable = throwable,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Tab.STACK_TRACE -> {
                    StackTraceBox(
                        throwable = throwable,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

enum class Tab(val title: StringResource) {
    REPORT_ERROR(title = Res.string.fatal_error_report_tab),
    STACK_TRACE(title = Res.string.fatal_error_stack_trace_tab);
}
