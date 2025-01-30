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

package com.neoutils.neorefex.feature.validator.model

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TestCaseQueue {

    private val signal = Channel<Unit>(capacity = Channel.CONFLATED)

    private val tests = LinkedHashMap<Uuid, TestCase>()
    private val mutex = Mutex()

    suspend fun enqueue(testCase: TestCase) {
        mutex.withLock {
            tests[testCase.uuid] = testCase
        }

        signal.trySend(Unit)
    }

    suspend fun enqueue(testCases: List<TestCase>) {
        mutex.withLock {
            tests.putAll(
                testCases.map {
                    it.uuid to it
                }
            )
        }

        signal.trySend(Unit)
    }

    suspend fun dequeue(uuid: Uuid) {
        mutex.withLock {
            tests.remove(uuid)
        }
    }

    suspend fun dequeue(): TestCase? {
        return mutex.withLock {
            val nextEntry = tests.entries.firstOrNull() ?: return null

            tests.remove(nextEntry.key)
            nextEntry.value
        }
    }

    suspend fun clear() = mutex.withLock {
        tests.clear()
    }

    suspend fun receive() = signal.receive()
}