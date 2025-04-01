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

import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.test_case_match_any
import com.neoutils.neoregex.core.resources.test_case_match_full
import com.neoutils.neoregex.core.resources.test_case_match_none
import com.neoutils.neoregex.feature.validator.model.TestCaseValidation
import org.jetbrains.compose.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class TestCaseUi(
    val uuid: Uuid,
    val title: String,
    val text: String,
    val case: Case,
    val validation: TestCaseValidation,
    val selected: Boolean,
) {
    enum class Case(val text: StringResource) {
        MATCH_ANY(Res.string.test_case_match_any),
        MATCH_FULL(Res.string.test_case_match_full),
        MATCH_NONE(Res.string.test_case_match_none),
    }
}