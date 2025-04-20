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

package com.neoutils.neoregex.feature.validator.usecase

import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.feature.validator.model.TestCaseValidation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ValidateUseCase {

    suspend operator fun invoke(
        testCase: TestCase,
        regex: Regex
    ): TestCaseValidation {

        val matches = withContext(Dispatchers.Default) {
            regex
                .findAll(testCase.text)
                .mapIndexedTo(mutableListOf()) { index, match ->
                    Match(
                        text = match.value,
                        range = match.range,
                        groups = match.groupValues.drop(n = 1),
                        number = index.inc(),
                    )
                }
        }

        return when (testCase.case) {
            TestCase.Case.MATCH_ANY -> {
                if (matches.isEmpty()) {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.ERROR,
                        matches = matches
                    )
                } else {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.SUCCESS,
                        matches = matches
                    )
                }
            }

            TestCase.Case.MATCH_FULL -> {
                if (regex.matches(testCase.text)) {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.SUCCESS,
                        matches = matches
                    )
                } else {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.ERROR,
                        matches = matches
                    )
                }
            }

            TestCase.Case.MATCH_NONE -> {
                if (matches.isEmpty()) {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.SUCCESS,
                        matches = matches
                    )
                } else {
                    TestCaseValidation(
                        testCase = testCase,
                        result = TestCaseValidation.Result.ERROR,
                        matches = matches
                    )
                }
            }
        }
    }
}