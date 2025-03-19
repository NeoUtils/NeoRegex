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

package com.neoutils.neoregex.core.database

import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.model.TestCase.Case
import com.neoutils.neoregex.core.database.db.PatternDatabase
import com.neoutils.neoregex.core.datasource.PatternDataSource
import com.neoutils.neoregex.core.datasource.model.Pattern
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
internal class PatternSqlDelightDataSource(
    private val database: PatternDatabase
) : PatternDataSource {

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun save(pattern: Pattern): Pattern {
        return database.transactionWithResult {
            val createAt = Clock.System.now().toEpochMilliseconds()

            database.patternEntityQueries.insertPattern(
                title = pattern.title,
                text = pattern.text,
                createAt = createAt
            )

            val patternId = database.patternEntityQueries.getLastInsertedId().executeAsOne()

            pattern.testCases.forEach { testCase ->
                database.testCaseEntityQueries.insertTestCase(
                    uuid = testCase.uuid.toHexString(),
                    patternId = patternId,
                    title = testCase.title,
                    text = testCase.text,
                    testCase = testCase.case.name,
                    createAt = createAt
                )
            }

            pattern.copy(
                id = patternId
            )
        }
    }

    override suspend fun get(id: Long): Pattern? {
        return database.transactionWithResult {
            val pattern = database.patternEntityQueries.getPatternById(id).executeAsOneOrNull()
            val testCases = database.testCaseEntityQueries.getTestCases(id).executeAsList()

            pattern?.let {
                Pattern(
                    id = pattern.id,
                    title = pattern.title,
                    text = pattern.text,
                    testCases = testCases.map {
                        TestCase(
                            uuid = Uuid.parseHex(it.uuid),
                            title = it.title,
                            text = it.text,
                            case = Case.valueOf(it.testCase)
                        )
                    }
                )
            }
        }
    }

    override suspend fun delete(id: Long) {
        database.transaction {
            database.testCaseEntityQueries.deleteTestCases(id)
            database.patternEntityQueries.deletePatternById(id)
        }
    }

    override suspend fun getAll(): List<Pattern> {
        return database.transactionWithResult {
            database.patternEntityQueries.getAllPatterns().executeAsList().map { pattern ->
                val testCases = database.testCaseEntityQueries.getTestCases(pattern.id).executeAsList()

                Pattern(
                    id = pattern.id,
                    title = pattern.title,
                    text = pattern.text,
                    testCases = testCases.map {
                        TestCase(
                            uuid = Uuid.parseHex(it.uuid),
                            title = it.title,
                            text = it.text,
                            case = Case.valueOf(it.testCase)
                        )
                    }
                )
            }
        }
    }

    override suspend fun changeName(id: Long, name: String) {
        database.transaction {
            database.patternEntityQueries.updatePatternTitle(
                title = name,
                id = id
            )
        }
    }

    override suspend fun update(
        patternId: Long,
        text: String,
        testCases: List<TestCase>
    ) {
        database.transaction {

            database.patternEntityQueries.updatePatternText(
                text = text,
                id = patternId
            )

            val savedTestCases = database.testCaseEntityQueries.getTestCases(patternId).executeAsList()

            database.testCaseEntityQueries.deleteTestCases(patternId)

            testCases.forEach { testCase ->
                val createAt = savedTestCases.find {
                    it.uuid == testCase.uuid.toHexString()
                }?.createAt

                database.testCaseEntityQueries.insertTestCase(
                    uuid = testCase.uuid.toHexString(),
                    patternId = patternId,
                    title = testCase.title,
                    text = testCase.text,
                    testCase = testCase.case.name,
                    createAt = createAt ?: Clock.System.now().toEpochMilliseconds()
                )
            }
        }
    }
}