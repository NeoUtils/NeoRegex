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

package com.neoutils.neorefex.feature.validator.model

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TestCase(
    val title: String = "",
    val text: String = "",
    val case: Case = Case.MATCH_ANY,
    val uuid: Uuid = Uuid.random(),
    val result: Result = Result.IDLE
) {

    val mustValidate = text.isNotEmpty() && result == Result.IDLE
    val testable = text.isNotEmpty()

    enum class Case(val text: String) {
        MATCH_ANY(text = "Match Any"),
        MATCH_ALL(text = "Match All"),
        MATCH_NONE(text = "Match None")
    }

    enum class Result {
        IDLE,
        RUNNING,
        SUCCESS,
        ERROR;

        val isRunning get() = this == RUNNING
        val isSuccess get() = this == SUCCESS
        val isError get() = this == ERROR
    }
}