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

package com.neoutils.neoregex.feature.saved

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neoregex.core.datasource.PatternsDataSource
import com.neoutils.neoregex.feature.saved.state.SavedUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SavedViewModel(
    private val patternsDataSource: PatternsDataSource
) : ScreenModel {

    val uiState = flow {
        emit(patternsDataSource.getAll())
    }.map {
        SavedUiState(
            patterns = it.map { pattern ->
                SavedUiState.Pattern(
                    name = pattern.title,
                    text = pattern.text
                )
            }
        )
    }.stateIn(
        scope = screenModelScope,
        initialValue = SavedUiState(patterns = listOf()),
        started = SharingStarted.WhileSubscribed()
    )
}