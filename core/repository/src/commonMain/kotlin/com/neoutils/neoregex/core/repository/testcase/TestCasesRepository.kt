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

package com.neoutils.neoregex.core.repository.testcase

import com.neoutils.neoregex.core.common.model.TestCase
import kotlinx.coroutines.flow.StateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface TestCasesRepository {

    val flow: StateFlow<List<TestCase>>
    val all : List<TestCase>

    fun update(uuid: Uuid, block: (TestCase) -> TestCase) : TestCase

    fun add(testCase: TestCase)
    fun get(uuid: Uuid) : TestCase?
    fun remove(uuid: Uuid)
    fun duplicate(uuid: Uuid): TestCase
    fun invalidate()
}