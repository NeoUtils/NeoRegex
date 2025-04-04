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

package com.neoutils.neoregex.core.repository.text

import com.neoutils.neoregex.core.common.manager.TextHistoryManager
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.repository.model.SampleState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TextStateRepositoryImpl(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    private val history: TextHistoryManager = TextHistoryManager()
) : TextStateRepository {

    private val text = MutableStateFlow(TextState())

    override val flow = text.combine(
        history.flow
    ) { text, history ->
        SampleState(
            text = text,
            history = history
        )
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = SampleState(
            text = text.value,
            history = history.flow.value
        )
    )

    init {
        coroutineScope.launch {
            text.collect {
                history.push(it)
            }
        }
    }

    override fun update(input: TextState) {
        text.value = input.copy(
            value = input.value
        )
    }

    override fun clear(initial: TextState) {
        history.clear()
        text.value = initial
    }

    override fun undo() {
        text.value = history.undo() ?: return
    }

    override fun redo() {
        text.value = history.redo() ?: return
    }
}