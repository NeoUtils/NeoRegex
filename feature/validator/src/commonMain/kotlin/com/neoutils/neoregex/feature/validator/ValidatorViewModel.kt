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
import com.neoutils.neoregex.core.common.model.TestCase
import com.neoutils.neoregex.core.common.util.ObservableMutableMap
import com.neoutils.neoregex.core.repository.pattern.PatternRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.feature.validator.model.TestCaseQueue
import com.neoutils.neoregex.feature.validator.model.TestPattern
import com.neoutils.neoregex.feature.validator.model.TestState
import com.neoutils.neoregex.feature.validator.component.TestCaseAction
import com.neoutils.neoregex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.feature.validator.component.toTestCaseUi
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

    private val testCaseQueue = TestCaseQueue()

    private val expanded = MutableStateFlow(testCasesRepository.all.firstOrNull()?.uuid)
    private val results = ObservableMutableMap<Uuid, TestState>()

    private var validationJob = mutableMapOf<Uuid, Job>()
    private var addToQueueJob = mutableMapOf<Uuid, Job>()

    private val testCasesUi = combine(
        expanded,
        testCasesRepository.flow,
        results.mapFlow,
    ) { expanded, testCases, results ->
        testCases.toTestCaseUi(
            results = results,
            expanded = expanded
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = testCasesRepository.all.toTestCaseUi(
            results = results,
            expanded = expanded.value
        )
    )

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
        testCasesUi,
        testPattern,
    ) { history, pattern, testCases, testPattern ->
        ValidatorUiState(
            pattern = pattern,
            history = history,
            testCases = testCases,
            testPattern = testPattern,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            history = patternRepository.historyFlow.value,
            pattern = patternRepository.flow.value,
            testCases = testCasesUi.value,
            testPattern = testPattern.value,
        )
    )

    init {
        setupQueueExecution()
        setupPatternListener()
    }

    private fun setupPatternListener() = screenModelScope.launch {
        testPattern.collectLatest { testPattern ->

            results.clear()

            testCaseQueue.clear()

            validationJob.forEach { it.value.cancel() }
            validationJob.clear()

            if (testPattern.isValid) {
                testCaseQueue.enqueue(
                    testCasesRepository.all.filterNot {
                        it.text.isEmpty()
                    }.reversed()
                )
            }
        }
    }

    private fun setupQueueExecution() = screenModelScope.launch {
        while (isActive) {
            val testCase = testCaseQueue.dequeue()

            if (testCase != null) {
                validationJob[testCase.uuid] = launch {

                    results[testCase.uuid] = TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.RUNNING
                    )

                    results[testCase.uuid] = validate(testCase)
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
    ): TestState {

        val result = regex.findAll(testCase.text)

        val matches = buildList {
            result.forEachIndexed { index, match ->
                add(
                    Match(
                        text = match.value,
                        range = match.range,
                        groups = match.groupValues.drop(n = 1),
                        number = index.inc(),
                    )
                )
            }
        }

        return when (testCase.case) {
            TestCase.Case.MATCH_ANY -> {
                if (matches.isEmpty()) {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.ERROR,
                        matches = matches
                    )
                } else {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.SUCCESS,
                        matches = matches
                    )
                }
            }

            TestCase.Case.MATCH_FULL -> {
                if (regex.matches(testCase.text)) {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.SUCCESS,
                        matches = matches
                    )
                } else {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.ERROR,
                        matches = matches
                    )
                }
            }

            TestCase.Case.MATCH_NONE -> {
                if (matches.isEmpty()) {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.SUCCESS,
                        matches = matches
                    )
                } else {
                    TestState(
                        uuid = testCase.uuid,
                        result = TestState.Result.ERROR,
                        matches = matches
                    )
                }
            }
        }
    }

    private fun addToQueue(
        newTestCase: TestCase,
        withDelay: Boolean = false
    ) = screenModelScope.launch {

        testCaseQueue.dequeue(newTestCase.uuid)

        validationJob[newTestCase.uuid]?.cancelAndJoin()
        validationJob.remove(newTestCase.uuid)

        results[newTestCase.uuid] = TestState(newTestCase.uuid)

        addToQueueJob[newTestCase.uuid]?.cancelAndJoin()
        addToQueueJob.remove(newTestCase.uuid)

        if (testPattern.value.isValid && newTestCase.text.isNotEmpty()) {
            addToQueueJob[newTestCase.uuid] = launch {
                if (withDelay) {
                    delay(DELAY_TYPING)
                }

                testCaseQueue.enqueue(newTestCase)
            }

            addToQueueJob[newTestCase.uuid]?.join()
            addToQueueJob.remove(newTestCase.uuid)
        }
    }

    private fun onRemoveTestCase(
        uuid: Uuid
    ) = screenModelScope.launch {

        testCaseQueue.dequeue(uuid)

        validationJob[uuid]?.cancel()
        validationJob.remove(uuid)

        results.remove(uuid)
    }

    fun onAction(action: ValidatorAction) {
        when (action) {
            is ValidatorAction.AddTestCase -> {
                expanded.value = action.newTestCase.uuid
                testCasesRepository.set(action.newTestCase)
            }
        }
    }

    fun onAction(action: TestCaseAction) {
        when (action) {
            is TestCaseAction.ChangeCase -> {

                val oldTestCase = testCasesRepository.get(action.uuid)

                val newTestCase = testCasesRepository
                    .update(action.uuid) {
                        it.copy(
                            case = action.case
                        )
                    }

                if (oldTestCase != newTestCase) {
                    addToQueue(
                        newTestCase
                    )
                }
            }

            is TestCaseAction.ChangeText -> {

                val oldTestCase = testCasesRepository.get(action.uuid)

                val newTestCase = testCasesRepository
                    .update(action.uuid) {
                        it.copy(
                            text = action.text
                        )
                    }

                if (oldTestCase != newTestCase) {
                    addToQueue(
                        newTestCase,
                        withDelay = true
                    )
                }
            }

            is TestCaseAction.ChangeTitle -> {
                testCasesRepository
                    .update(action.uuid) {
                        it.copy(
                            title = action.title
                        )
                    }
            }

            is TestCaseAction.Collapse -> {
                expanded.value = null
            }

            is TestCaseAction.Delete -> {
                expanded.value = null
                testCasesRepository.remove(action.uuid)
                onRemoveTestCase(action.uuid)
            }

            is TestCaseAction.Duplicate -> {
                addToQueue(
                    testCasesRepository
                        .duplicate(action.uuid)
                        .also {
                            expanded.value = it.uuid
                        }
                )
            }

            is TestCaseAction.Expanded -> {
                expanded.value = action.uuid
            }
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
