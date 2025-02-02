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

import com.neoutils.neoregex.core.common.model.History
import com.neoutils.neoregex.core.common.model.Inputs
import com.neoutils.neoregex.core.sharedui.component.Performance
import com.neoutils.neoregex.core.sharedui.model.Match

data class MatcherUiState(
    val inputs: Inputs = Inputs(),
    val history: History = History(),
    val result: Result = Result.Success(),
    val performance: Performance = Performance()
) {

    sealed class Result {
        data class Success(
            val matches: List<Match> = listOf(),
            val performance: Performance = Performance()
        ) : Result()

        data class Failure(
            val error: String,
        ) : Result()
    }
}
