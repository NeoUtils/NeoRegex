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

@file:OptIn(ExperimentalUuidApi::class)

package com.neoutils.neorefex.feature.validator.state

import com.neoutils.neorefex.feature.validator.model.TestPattern
import com.neoutils.neoregex.core.common.model.History
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.model.Text
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ValidatorUiState(
    val testCases: List<TestCase>,
    val pattern: Text,
    val history: History,
    val expanded: Uuid? = null,
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
    testCases: List<TestCase>,
    pattern: Text,
    history: History,
    expanded: Uuid? = null,
    testPattern: TestPattern,
): ValidatorUiState {

    val testableCases = testCases.filter { it.testable }

    return ValidatorUiState(
        testCases = testCases,
        pattern = pattern,
        history = history,
        expanded = expanded,
        error = testPattern.regex.exceptionOrNull()?.message,
        result = when {
            testPattern.regex.isFailure -> {
                ValidatorUiState.Result.ERROR
            }

            testPattern.isInvalid or testableCases.isEmpty() -> {
                ValidatorUiState.Result.WAITING
            }

            testableCases.any { it.result.isRunning } -> {
                ValidatorUiState.Result.RUNNING
            }

            testableCases.any { it.result.isError } -> {
                ValidatorUiState.Result.ERROR
            }

            testableCases.all { it.result.isSuccess } -> {
                ValidatorUiState.Result.SUCCESS
            }

            else -> {
                ValidatorUiState.Result.WAITING
            }
        }
    )
}