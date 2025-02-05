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

package com.neoutils.neoregex.feature.validator.model

import com.neoutils.neoregex.core.common.model.Match
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TestState(
    val uuid: Uuid,
    val result: Result = TestState.Result.IDLE,
    val matches: List<Match> = listOf()
) {
    enum class Result {
        IDLE,
        RUNNING,
        SUCCESS,
        ERROR;

        val isRunning get() = this == RUNNING
        val isSuccess get() = this == SUCCESS
        val isError get() = this == ERROR
        val isIdle get() = this == IDLE
    }
}