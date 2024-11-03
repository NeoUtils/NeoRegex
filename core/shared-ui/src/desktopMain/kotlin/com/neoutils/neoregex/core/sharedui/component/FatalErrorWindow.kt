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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.fatal_error_title
import com.neoutils.neoregex.core.resources.report_error
import com.neoutils.neoregex.core.resources.stack_trace
import com.neoutils.neoregex.core.sharedui.extension.updateSize
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ApplicationScope.FatalErrorWindow(
    throwable: Throwable
) {
    val state = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(400.dp, 320.dp),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(
            Res.string.fatal_error_title,
            throwable::class.java.name
        ),
        state = state,
        resizable = false,
    ) {

        Column {

            var current by remember { mutableStateOf(Tab.REPORT_ERROR) }

            LaunchedEffect(current) {
                when (current) {
                    Tab.REPORT_ERROR -> {
                        state.updateSize(DpSize(400.dp, 320.dp))
                    }

                    Tab.STACK_TRACE -> {
                        state.updateSize(DpSize(800.dp, 600.dp))
                    }
                }
            }

            val selectedTabIndex = remember(current) { Tab.entries.indexOf(current) }

            TabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                Tab.entries.forEach {
                    Tab(
                        text = { Text(stringResource(it.title)) },
                        selected = current == it,
                        onClick = {
                            current = it
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
    REPORT_ERROR(title = Res.string.report_error),
    STACK_TRACE(title = Res.string.stack_trace);
}
