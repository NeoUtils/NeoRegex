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
import com.neoutils.neoregex.core.common.util.ObservableMutableMap
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class TestCasesRepositoryImpl : TestCasesRepository {

    private val testCases = ObservableMutableMap(TestCase().toPair())

    override val flow = testCases.valuesFlow

    override val all get() = flow.value

    override fun set(testCase: TestCase) {
        testCases[testCase.uuid] = testCase
    }

    override fun get(uuid: Uuid) = testCases[uuid]

    override fun update(
        uuid: Uuid,
        block: (TestCase) -> TestCase
    ): TestCase {

        testCases[uuid] = block(checkNotNull(get(uuid)))

        return checkNotNull(get(uuid))
    }

    override fun remove(uuid: Uuid) {
        testCases.remove(uuid)
    }

    override fun duplicate(uuid: Uuid): TestCase {

        val newUuid = Uuid.random()

        testCases[newUuid] = checkNotNull(get(uuid)).copy(uuid = newUuid)

        return checkNotNull(get(newUuid))
    }

    override fun clear() {

        testCases.clear()

        val uuid = Uuid.random()

        testCases[uuid] = TestCase(uuid = uuid)
    }
}