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

package com.neoutils.neoregex.feature.validator.state

import com.neoutils.neoregex.core.common.model.HistoryState
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.feature.validator.component.TestCaseUi
import com.neoutils.neoregex.feature.validator.model.TestPattern

data class ValidatorUiState(
    val testCases: List<TestCaseUi>,
    val pattern: TextState,
    val history: HistoryState,
    val error: String? = null,
    val result: Result = Result.WAITING
) {
    enum class Result {
        WAITING,
        RUNNING,
        SUCCESS,
        ERROR
    }
}

fun ValidatorUiState(
    testCases: List<TestCaseUi>,
    testPattern: TestPattern,
    pattern: TextState,
    history: HistoryState,
): ValidatorUiState {

    val testableCases = testCases.filter { it.text.isNotEmpty() }

    return ValidatorUiState(
        testCases = testCases,
        pattern = pattern,
        history = history,
        error = testPattern.regex.exceptionOrNull()?.message,
        result = when {
            testPattern.regex.isFailure -> {
                ValidatorUiState.Result.ERROR
            }

            testPattern.isInvalid or testableCases.isEmpty() -> {
                ValidatorUiState.Result.WAITING
            }

            testableCases.any { it.validation.result.isRunning } -> {
                ValidatorUiState.Result.RUNNING
            }

            testableCases.any { it.validation.result.isError } -> {
                ValidatorUiState.Result.ERROR
            }

            testableCases.all { it.validation.result.isSuccess } -> {
                ValidatorUiState.Result.SUCCESS
            }

            else -> {
                ValidatorUiState.Result.WAITING
            }
        }
    )
}