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

package com.neoutils.neoregex.core.repository.pattern

import com.neoutils.neoregex.core.common.manager.TextHistoryManager
import com.neoutils.neoregex.core.common.model.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PatternRepositoryImpl(
    private val textHistoryManager: TextHistoryManager = TextHistoryManager(),
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) : PatternRepository {

    private val _flow = MutableStateFlow(Text())
    override val flow = _flow.asStateFlow()

    override val historyFlow = textHistoryManager.flow

    init {
        _flow.onEach {
            textHistoryManager.push(it)
        }.launchIn(coroutineScope)
    }

    override fun update(input: Text) {
        textHistoryManager.unlock()
        _flow.value = input
    }

    override fun undo() {
        _flow.value = textHistoryManager.undo() ?: return
    }

    override fun redo() {
        _flow.value = textHistoryManager.redo() ?: return
    }
}