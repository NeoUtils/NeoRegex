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

package com.neoutils.neorefex.feature.validator

import androidx.compose.ui.text.input.TextFieldValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neorefex.feature.validator.action.ValidatorAction
import com.neoutils.neorefex.feature.validator.model.TestCase
import com.neoutils.neorefex.feature.validator.model.TestCaseQueue
import com.neoutils.neorefex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.sharedui.extension.toTextFieldValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ValidatorViewModel : ScreenModel {

    private val pattern = MutableStateFlow(TextFieldValue())
    private val testCases = MutableStateFlow(listOf(TestCase()))
    private val expanded = MutableStateFlow<Uuid?>(testCases.value.first().uuid)

    private val testCaseQueue = TestCaseQueue()

    private var validationJob = mutableMapOf<Uuid, Job>()
    private var addToQueueJob = mutableMapOf<Uuid, Job>()

    val uiState = combine(
        pattern,
        testCases,
        expanded
    ) { pattern, testCases, selected ->
        ValidatorUiState(
            pattern = pattern,
            testCases = testCases,
            expanded = selected
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            pattern = pattern.value,
            testCases = testCases.value,
            expanded = expanded.value
        )
    )

    init {
        setupQueueExecution()
    }

    private fun setupQueueExecution() = screenModelScope.launch {
        while (isActive) {
            val testCase = testCaseQueue.dequeue()

            if (testCase != null) {
                validationJob[testCase.uuid] = launch {
                    testCases.replace(testCase.uuid) {
                        it.copy(
                            result = TestCase.Result.RUNNING
                        )
                    }

                    delay(timeMillis = 5000)

                    testCases.replace(testCase.uuid) {
                        it.copy(
                            result = TestCase.Result.SUCCESS
                        )
                    }
                }

                // wait execution
                validationJob[testCase.uuid]?.join()
                validationJob.remove(testCase.uuid)
            } else {

                // wait new test cases
                testCaseQueue.receive()
            }
        }
    }

    fun onAction(action: ValidatorAction) {
        when (action) {
            is ValidatorAction.ExpandedTestCase -> {
                expanded.value = action.uuid
            }

            is ValidatorAction.RemoveTestCase -> {
                removeTestCase(action.uuid)
            }

            is ValidatorAction.UpdateTestCase -> {
                updateTestCase(action)
            }

            is ValidatorAction.AddTestCase -> {
                testCases.update { it + action.newTestCase }
                expanded.value = action.newTestCase.uuid
            }
        }
    }

    private fun updateTestCase(
        action: ValidatorAction.UpdateTestCase
    ) {
        val oldTestCase = testCases.value.find {
            it.uuid == action.newTestCase.uuid
        }

        val mustValidate = when {
            oldTestCase == null -> true
            oldTestCase.text != action.newTestCase.text -> true
            oldTestCase.case != action.newTestCase.case -> true
            else -> false
        }

        val newTestCase = action.newTestCase.copy(
            result = if (mustValidate) {
                TestCase.Result.IDLE
            } else {
                action.newTestCase.result
            }
        )

        testCases.update { testCases ->
            testCases.map { oldTestCase ->
                newTestCase.takeIf {
                    it.uuid == oldTestCase.uuid
                } ?: oldTestCase
            }
        }

        if (mustValidate) {
            addToQueue(newTestCase)
        }
    }

    private fun removeTestCase(
        uuid: Uuid
    ) = screenModelScope.launch {
        testCases.update { testCases ->
            testCases.filter {
                it.uuid != uuid
            }
        }

        testCaseQueue.dequeue(uuid)

        validationJob[uuid]?.cancel()
        validationJob.remove(uuid)
    }

    private fun addToQueue(
        newTestCase: TestCase
    ) = screenModelScope.launch {

        // remove from queue
        testCaseQueue.dequeue(newTestCase.uuid)

        // stop execution
        validationJob[newTestCase.uuid]?.cancel()
        validationJob.remove(newTestCase.uuid)

        // add to queue

        addToQueueJob[newTestCase.uuid]?.cancel()

        if (newTestCase.mustValidate) {
            addToQueueJob[newTestCase.uuid] = launch {
                delay(DELAY_TYPING)
                testCaseQueue.enqueue(newTestCase)
            }

            addToQueueJob[newTestCase.uuid]?.join()
            addToQueueJob.remove(newTestCase.uuid)
        }
    }

    fun onAction(action: FooterAction) {
        when (action) {
            is FooterAction.History.Redo -> {
                // TODO: implement later
            }

            is FooterAction.History.Undo -> {
                // TODO: implement later
            }

            is FooterAction.UpdateRegex -> {
                screenModelScope.launch {

                    pattern.value = action.textState.toTextFieldValue()

                    // invalidate

                    testCases.update { testCases ->
                        testCases.map {
                            it.copy(
                                result = TestCase.Result.IDLE
                            )
                        }
                    }

                    // clear queue

                    testCaseQueue.clear()

                    // stop validation

                    validationJob.forEach { it.value.cancel() }
                    validationJob.clear()

                    // add to queue

                    addToQueueJob[Uuid.NIL] = launch {
                        delay(DELAY_TYPING)
                        testCaseQueue.enqueue(
                            testCases.value.filter {
                                it.mustValidate
                            }
                        )
                    }

                    addToQueueJob[Uuid.NIL]?.join()
                    addToQueueJob.remove(Uuid.NIL)
                }
            }
        }
    }

    companion object {
        private const val DELAY_TYPING = 500L
    }
}

@OptIn(ExperimentalUuidApi::class)
private fun MutableStateFlow<List<TestCase>>.replace(
    uuid: Uuid,
    block: (TestCase) -> TestCase
) = update { testCases ->
    testCases.map { testCase ->
        if (testCase.uuid == uuid) {
            block(testCase)
        } else {
            testCase
        }
    }
}

