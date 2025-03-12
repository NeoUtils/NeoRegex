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

package com.neoutils.neoregex.core.datasource.model

import com.neoutils.neoregex.core.common.model.TestCase.Case

data class Pattern(
    val id: Long? = null,
    val title: String,
    val text: String,
    val testCases: List<TestCase>
) {
    data class TestCase(
        val title: String,
        val text: String,
        val case: Case = Case.MATCH_ANY,
    )
}