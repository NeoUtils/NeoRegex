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

package com.neoutils.neoregex.feature.matcher.state

import androidx.compose.ui.text.input.TextFieldValue
import com.neoutils.neoregex.core.sharedui.component.MatchesInfos
import com.neoutils.neoregex.core.sharedui.model.Match
import com.neoutils.neoregex.feature.matcher.model.Target

data class MatcherUiState(
    val target: Target? = null,
    val text: TextFieldValue = TextFieldValue(),
    val regex: TextFieldValue = TextFieldValue(),
    val history: History = History(),
    val matchResult: MatchResult = MatchResult.Success(),
) {
    data class History(
        val canUndo: Boolean = false,
        val canRedo: Boolean = false
    )

    sealed class MatchResult {

        abstract val infos: MatchesInfos?

        data class Success(
            val matches: List<Match> = listOf(),
            override val infos: MatchesInfos? = MatchesInfos.create(),
        ) : MatchResult()

        data class Failure(
            val error: String,
            override val infos: MatchesInfos? = MatchesInfos.create()
        ) : MatchResult()
    }
}

val MatcherUiState.MatchResult.matches
    get() = when (this) {
        is MatcherUiState.MatchResult.Failure -> listOf()
        is MatcherUiState.MatchResult.Success -> matches
    }

val MatcherUiState.MatchResult.error
    get() = when (this) {
        is MatcherUiState.MatchResult.Failure -> error
        is MatcherUiState.MatchResult.Success -> ""
    }
