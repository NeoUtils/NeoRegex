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

package com.neoutils.neoregex

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neoregex.core.datasource.PatternDataSource
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.repository.pattern.PatternRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import kotlinx.coroutines.launch

class AppViewModel(
    private val patternRepository: PatternRepository,
    private val testCasesRepository: TestCasesRepository,
    private val patternDataSource: PatternDataSource
) : ScreenModel {

    fun clear() {
        patternRepository.clear()
        testCasesRepository.clear()
    }

    fun save() = screenModelScope.launch {
        patternDataSource.save(
            Pattern(
                id = -1,
                title = "test",
                text = patternRepository.flow.value.text,
                testCases = testCasesRepository.all.map {
                    Pattern.TestCase(
                        title = it.title,
                        text = it.text,
                        case = it.case
                    )
                }
            )
        )

        println(patternDataSource.getAll())
    }
}