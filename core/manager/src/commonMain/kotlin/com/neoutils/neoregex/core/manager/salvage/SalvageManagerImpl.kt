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
    ) { opened, pattern, _, _ ->
        opened?.let {
            patternDataSource.get(opened)?.let {
                Salvage(
                    id = checkNotNull(it.id),
                    name = it.title,
                    updated = pattern.text == it.text
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

        uuid.value = Uuid.random()

        patternDataSource.changeName(id, name)
    }

    override suspend fun save(name: String) {
        val pattern = patternDataSource.save(
            Pattern(
                title = name,
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

        opened.value = pattern.id
    }
}