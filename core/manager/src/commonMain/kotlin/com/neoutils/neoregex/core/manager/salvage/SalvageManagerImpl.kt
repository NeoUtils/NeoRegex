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

package com.neoutils.neoregex.core.manager.salvage

import com.neoutils.neoregex.core.common.model.Salvage
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.datasource.PatternDataSource
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.repository.pattern.PatternRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SalvageManagerImpl(
    private val patternDataSource: PatternDataSource,
    private val patternRepository: PatternRepository,
    private val testCasesRepository: TestCasesRepository
) : SalvageManager {

    private val opened = MutableStateFlow<Long?>(null)
    private val uuid = MutableStateFlow(Uuid.random())

    override val salvage = combine(
        opened,
        patternRepository.flow,
        testCasesRepository.flow,
        uuid
    ) { opened, pattern, testCases, _ ->
        opened?.let { patternId ->
            patternDataSource.get(patternId)?.let { savedPattern ->
                Salvage(
                    id = checkNotNull(savedPattern.id),
                    name = savedPattern.title,
                    updated = pattern.text == savedPattern.text &&  savedPattern.testCases.takeIf {
                        it.size == testCases.size
                    }?.let { savedTestCases ->
                        testCases
                            .sortedBy { it.uuid.toHexString() }
                            .zip(savedTestCases.sortedBy { it.uuid.toHexString() })
                            .all { (current, saved) -> current == saved }
                    } ?: false
                )
            }
        }
    }

    override fun open(id: Long) {
        opened.value = id
    }

    override fun close() {
        opened.value = null
    }

    override suspend fun changeName(name: String) {
        val id = opened.value ?: return

        patternDataSource.changeName(id, name)

        uuid.value = Uuid.random()
    }

    override suspend fun update() {
        val id = opened.value ?: return

        patternDataSource.update(
            patternId = id,
            text = patternRepository.flow.value.text,
            testCases = testCasesRepository.all.map {
                TestCase(
                    uuid = it.uuid,
                    title = it.title,
                    text = it.text,
                    case = it.case
                )
            }
        )

        uuid.value = Uuid.random()
    }

    override suspend fun save(name: String) {
        val pattern = patternDataSource.save(
            Pattern(
                title = name,
                text = patternRepository.flow.value.text,
                testCases = testCasesRepository.all.map {
                    TestCase(
                        title = it.title,
                        text = it.text,
                        case = it.case,
                        uuid = it.uuid
                    )
                }
            )
        )

        opened.value = pattern.id
    }
}