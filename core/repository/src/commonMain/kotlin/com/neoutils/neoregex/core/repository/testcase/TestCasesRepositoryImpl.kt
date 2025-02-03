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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class TestCasesRepositoryImpl : TestCasesRepository {

    private val _flow = MutableStateFlow(listOf(TestCase()))
    override val flow = _flow.asStateFlow()

    override val all get() = flow.value

    override fun add(testCase: TestCase) {
        _flow.update {
            it + testCase
        }
    }

    override fun get(
        uuid: Uuid
    ) = flow.value.find {
        it.uuid == uuid
    }

    override fun update(
        uuid: Uuid,
        block: (TestCase) -> TestCase
    ): TestCase {
        _flow.update {
            it.map { testCase ->
                if (uuid == testCase.uuid) {
                    block(testCase)
                } else {
                    testCase
                }
            }
        }

        return checkNotNull(get(uuid))
    }

    override fun update(newTestCase: TestCase) {
        _flow.update {
            it.map { testCase ->
                if (newTestCase.uuid == testCase.uuid) {
                    newTestCase
                } else {
                    testCase
                }
            }
        }
    }

    override fun remove(uuid: Uuid) {
        _flow.update {
            it.filter { testCase ->
                testCase.uuid != uuid
            }
        }
    }

    override fun duplicate(uuid: Uuid): TestCase {

        val newUuid = Uuid.random()

        val index = all.indexOfFirst { testCase -> testCase.uuid == uuid }

        val newTestCase = all[index].copy(uuid = newUuid)

        val before = all.subList(0, index.inc())
        val after = all.subList(index.inc(), all.size)

        _flow.value = before + newTestCase + after

        return checkNotNull(get(newUuid))
    }

    override fun invalidate() {
        _flow.update {
            it.map { testCase ->
                testCase.copy(
                    result = TestCase.Result.IDLE
                )
            }
        }
    }
}