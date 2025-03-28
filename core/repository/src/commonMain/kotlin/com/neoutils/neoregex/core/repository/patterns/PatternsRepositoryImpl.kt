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

package com.neoutils.neoregex.core.repository.patterns

import com.neoutils.neoregex.core.datasource.PatternsDataSource
import com.neoutils.neoregex.core.datasource.model.Pattern
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class PatternsRepositoryImpl(
    private val patternsDataSource: PatternsDataSource
) : PatternsRepository {

    private val uuid = MutableStateFlow(Uuid.random())

    override val flow = uuid.map { patternsDataSource.getAll() }

    override suspend fun delete(id: Long) {
        patternsDataSource.delete(id)
        uuid.value = Uuid.random()
    }

    override suspend fun save(pattern: Pattern): Pattern {
        val savedPattern = patternsDataSource.save(pattern)

        uuid.value = Uuid.random()

        return savedPattern
    }

    override suspend fun get(id: Long) = patternsDataSource.get(id)

    override suspend fun update(
        id: Long,
        block: (Pattern) -> Pattern
    ): Pattern {
        return patternsDataSource.update(id, block)
    }
}