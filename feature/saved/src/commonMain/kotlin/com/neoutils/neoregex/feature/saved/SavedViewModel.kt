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
import com.neoutils.neoregex.core.manager.model.Navigation
import com.neoutils.neoregex.core.manager.navigator.NavigationManager
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.repository.patterns.PatternsRepository
import com.neoutils.neoregex.feature.saved.state.SavedUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedViewModel(
    private val patternsRepository: PatternsRepository,
    private val salvageManager: SalvageManager,
    private val navigationManager: NavigationManager
) : ScreenModel {

    val uiState = combine(
        patternsRepository.flow,
        salvageManager.flow
    ) { patterns, saved ->
        SavedUiState(
            patterns = patterns.map { pattern ->
                SavedUiState.Pattern(
                    id = checkNotNull(pattern.id),
                    title = pattern.title,
                    text = pattern.pattern,
                    opened = pattern.id == saved?.id
                )
            }
        )
    }.stateIn(
        scope = screenModelScope,
        initialValue = SavedUiState(patterns = listOf()),
        started = SharingStarted.WhileSubscribed()
    )

    fun open(id: Long) = screenModelScope.launch {
        salvageManager.open(id)
        navigationManager.emit(Navigation.Event.Invalidate(pop = 1))
    }

    fun delete(id: Long) = screenModelScope.launch {
        salvageManager.delete(id)
    }

    fun changeTitle(id: Long, title: String) = screenModelScope.launch {
        patternsRepository.update(id) {
            it.copy(
                title = title
            )
        }
    }
}