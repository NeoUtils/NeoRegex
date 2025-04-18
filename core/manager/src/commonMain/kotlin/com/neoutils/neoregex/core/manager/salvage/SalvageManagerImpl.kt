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
import com.neoutils.neoregex.core.common.model.Opened
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.datasource.model.Pattern
import com.neoutils.neoregex.core.dispatcher.model.Navigation
import com.neoutils.neoregex.core.dispatcher.navigator.NavigationManager
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.patterns.PatternsRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.repository.text.TextStateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class SalvageManagerImpl(
    private val patternsRepository: PatternsRepository,
    private val patternStateRepository: PatternStateRepository,
    private val testCasesRepository: TestCasesRepository,
    private val textStateRepository: TextStateRepository,
    private val navigationManager: NavigationManager
) : SalvageManager {

    private val opened = MutableStateFlow<Long?>(null)

    override val flow = combine(
        opened,
        patternStateRepository.flow,
        testCasesRepository.flow,
        patternsRepository.flow,
    ) { openedPatternId, pattern, testCases, patterns ->
        openedPatternId?.let {
            patterns.find {
                it.id == openedPatternId
            }?.let { savedPattern ->
                val updated =
                    pattern.text.value == savedPattern.text &&
                            testCases deepEquals savedPattern.testCases
                Opened(
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

        val pattern = patternsRepository.get(id) ?: return

        textStateRepository.clear()
        patternStateRepository.clear(TextState(pattern.text))
        testCasesRepository.clear()

        sync()
    }

    override suspend fun close() {
        opened.value = null

        textStateRepository.clear()
        patternStateRepository.clear()
        testCasesRepository.clear()

        navigationManager.emit(Navigation.Event.Invalidate())
    }

    override suspend fun update(block: (Pattern) -> Pattern) {
        val id = opened.value ?: return

        patternsRepository.update(id, block)
    }

    override suspend fun update() {
        val id = opened.value ?: return

        patternsRepository.update(
            id = id
        ) { pattern ->
            pattern.copy(
                text = patternStateRepository.pattern.text.value,
                testCases = testCasesRepository.all.map { testCase ->
                    TestCase(
                        uuid = testCase.uuid,
                        title = testCase.title,
                        text = testCase.text,
                        case = testCase.case
                    )
                }
            )
        }
    }

    override suspend fun sync() {
        val id = opened.value ?: return

        val pattern = patternsRepository.get(id) ?: return

        patternStateRepository.update(TextState(pattern.text))
        testCasesRepository.setAll(pattern.testCases)
    }

    override suspend fun delete(id: Long) {

        if (id == opened.value) close()

        patternsRepository.delete(id)
    }

    override suspend fun save(name: String) {
        val pattern = patternsRepository.save(
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
