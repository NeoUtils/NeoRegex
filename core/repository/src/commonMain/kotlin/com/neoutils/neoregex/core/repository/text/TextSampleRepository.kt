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

import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.repository.model.SampleState
import kotlinx.coroutines.flow.StateFlow

interface TextSampleRepository {

    val flow : StateFlow<SampleState>
    val sample get() = flow.value

    fun update(input: TextState)
    fun clear(initial: TextState = TextState())

    fun undo()
    fun redo()
}