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

import com.neoutils.neoregex.core.common.extension.deepEquals
import com.neoutils.neoregex.core.common.model.Salvage
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.datasource.PatternsDataSource
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class SalvageManagerImpl(
    private val patternsDataSource: PatternsDataSource,
    private val patternStateRepository: PatternStateRepository,
    private val testCasesRepository: TestCasesRepository
) : SalvageManager {

    private val opened = MutableStateFlow<Long?>(null)
    private val uuid = MutableStateFlow(Uuid.random())

    override val salvage = combine(
        opened,
        patternStateRepository.flow,
        testCasesRepository.flow,
        uuid
    ) { opened, pattern, testCases, _ ->
        opened?.let { patternId ->
            patternsDataSource.get(patternId)?.let { savedPattern ->

                val updated =
                    pattern.text.value == savedPattern.text &&
                            testCases deepEquals savedPattern.testCases

                Salvage(
                    id = checkNotNull(savedPattern.id),
                    name = savedPattern.title,
                    updated = updated,
                    canUpdate = !updated && pattern.isValid,
                )
            }
        }
    }

    override val canSave = opened.combine(
        patternStateRepository.flow
    ) { opened, pattern ->
        opened == null && pattern.isValid
    }

    override suspend fun open(id: Long) {
        opened.value = id

        val pattern = patternsDataSource.get(id) ?: return

        patternStateRepository.clear(TextState(pattern.text))
        testCasesRepository.clear()
    }

    override fun close() {
        opened.value = null

        patternStateRepository.clear()
        testCasesRepository.clear()
    }

    override suspend fun changeName(name: String) {
        val id = opened.value ?: return

        patternsDataSource.changeName(id, name)

        uuid.value = Uuid.random()
    }

    override suspend fun update() {
        val id = opened.value ?: return

        patternsDataSource.update(
            patternId = id,
            text = patternStateRepository.pattern.text.value,
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

    override suspend fun sync() {
        val id = opened.value ?: return

        val pattern = patternsDataSource.get(id) ?: return

        patternStateRepository.update(TextState(pattern.text))
        testCasesRepository.setAll(pattern.testCases)
    }

    override suspend fun save(name: String) {
        val pattern = patternsDataSource.save(
            Pattern(
                title = name,
                text = patternStateRepository.pattern.text.value,
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
