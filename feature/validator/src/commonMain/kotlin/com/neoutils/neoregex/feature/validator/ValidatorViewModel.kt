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

package com.neoutils.neoregex.feature.validator

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.neoutils.neoregex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.feature.validator.model.TestCaseQueue
import com.neoutils.neoregex.feature.validator.model.TestPattern
import com.neoutils.neoregex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.repository.pattern.PatternRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(
    ExperimentalCoroutinesApi::class,
    ExperimentalUuidApi::class,
    FlowPreview::class
)
class ValidatorViewModel(
    private val patternRepository: PatternRepository,
    private val testCasesRepository: TestCasesRepository
) : ScreenModel {

    private val expanded = MutableStateFlow(testCasesRepository.all.firstOrNull()?.uuid)

    private val testCaseQueue = TestCaseQueue()

    private var validationJob = mutableMapOf<Uuid, Job>()
    private var addToQueueJob = mutableMapOf<Uuid, Job>()

    private val testPattern = patternRepository.flow
        .debounce(DELAY_TYPING)
        .distinctUntilChangedBy { it.text }
        .mapLatest { TestPattern(it.text) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TestPattern(patternRepository.flow.value.text)
        )

    val uiState = combine(
        patternRepository.historyFlow,
        patternRepository.flow,
        testCasesRepository.flow,
        testPattern,
        expanded,
    ) { history, pattern, testCases, testPattern, selected ->
        ValidatorUiState(
            pattern = pattern,
            history = history,
            testCases = testCases,
            testPattern = testPattern,
            expanded = selected
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            history = patternRepository.historyFlow.value,
            pattern = patternRepository.flow.value,
            testCases = testCasesRepository.flow.value,
            testPattern = testPattern.value,
            expanded = expanded.value
        )
    )

    init {
        setupQueueExecution()
        setupPatternListener()
    }

    private fun setupPatternListener() = screenModelScope.launch {
        testPattern.collectLatest { testPattern ->

            // invalidate
            testCasesRepository.invalidate()

            // clear queue
            testCaseQueue.clear()

            // stop validation
            validationJob.forEach { it.value.cancel() }
            validationJob.clear()

            // add to queue
            if (testPattern.isValid) {
                testCaseQueue.enqueue(
                    testCasesRepository.all.filter {
                        it.mustValidate
                    }
                )
            }
        }
    }

    private fun setupQueueExecution() = screenModelScope.launch {
        while (isActive) {
            val testCase = testCaseQueue.dequeue()

            if (testCase != null) {
                validationJob[testCase.uuid] = launch {
                    testCasesRepository.update(testCase.uuid) {
                        it.copy(
                            result = TestCase.Result.RUNNING
                        )
                    }

                    testCasesRepository.update(testCase.uuid) {
                        it.copy(
                            result = validate(testCase)
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

    private fun validate(
        testCase: TestCase,
        regex: Regex = testPattern.value.regex.getOrThrow()
    ): TestCase.Result {
        return when (testCase.case) {
            TestCase.Case.MATCH_ANY -> {
                if (regex.find(testCase.text) != null) {
                    TestCase.Result.SUCCESS
                } else {
                    TestCase.Result.ERROR
                }
            }

            TestCase.Case.MATCH_ALL -> {
                if (regex.matches(testCase.text)) {
                    TestCase.Result.SUCCESS
                } else {
                    TestCase.Result.ERROR
                }
            }

            TestCase.Case.MATCH_NONE -> {
                if (regex.find(testCase.text) == null) {
                    TestCase.Result.SUCCESS
                } else {
                    TestCase.Result.ERROR
                }
            }
        }
    }

    fun onAction(action: ValidatorAction) {
        when (action) {
            is ValidatorAction.ExpandedTestCase -> {
                expanded.value = action.targetUuid
            }

            is ValidatorAction.CollapseTestCase -> {
                if (expanded.value == action.targetUuid) {
                    expanded.value = null
                }
            }

            is ValidatorAction.RemoveTestCase -> {
                removeTestCase(action.targetUuid)
            }

            is ValidatorAction.UpdateTestCase -> {
                updateTestCase(action.newTestCase)
            }

            is ValidatorAction.AddTestCase -> {
                testCasesRepository.set(action.newTestCase)
                expanded.value = action.newTestCase.uuid
            }

            is ValidatorAction.Duplicate -> {
                expanded.value = testCasesRepository
                    .duplicate(action.targetUuid).uuid
            }
        }
    }

    private fun updateTestCase(
        newTestCase: TestCase,
        testPattern: TestPattern = this.testPattern.value
    ) {
        val oldTestCase = testCasesRepository.get(newTestCase.uuid)

        val mustValidate = when {
            oldTestCase == null -> true
            oldTestCase.text != newTestCase.text -> true
            oldTestCase.case != newTestCase.case -> true
            else -> false
        }

        val testCase = newTestCase.copy(
            result = if (mustValidate) {
                TestCase.Result.IDLE
            } else {
                newTestCase.result
            }
        )

        testCasesRepository.set(testCase)

        if (mustValidate && testPattern.isValid) {
            addToQueue(testCase)
        }
    }

    private fun removeTestCase(
        uuid: Uuid
    ) = screenModelScope.launch {

        testCasesRepository.remove(uuid)

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
                patternRepository.redo()
            }

            is FooterAction.History.Undo -> {
                patternRepository.undo()
            }

            is FooterAction.UpdateRegex -> {
                patternRepository.update(action.text)
            }
        }
    }

    companion object {
        private const val DELAY_TYPING = 500L
    }
}
