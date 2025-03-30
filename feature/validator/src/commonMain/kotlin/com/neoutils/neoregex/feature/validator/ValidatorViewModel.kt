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
import com.neoutils.neoregex.core.repository.pattern.PatternStateRepository
import com.neoutils.neoregex.core.repository.testcase.TestCasesRepository
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.feature.validator.action.ValidatorAction
import com.neoutils.neoregex.feature.validator.component.TestCaseAction
import com.neoutils.neoregex.feature.validator.component.toTestCaseUi
import com.neoutils.neoregex.feature.validator.model.TestCaseQueue
import com.neoutils.neoregex.feature.validator.model.TestCaseValidation
import com.neoutils.neoregex.feature.validator.model.TestPattern
import com.neoutils.neoregex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.feature.validator.usecase.ValidateUseCase
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
    private val patternStateRepository: PatternStateRepository,
    private val testCasesRepository: TestCasesRepository,
    private val validateUserCase: ValidateUseCase,
    private val testCaseQueue: TestCaseQueue
) : ScreenModel {

    private val expanded = MutableStateFlow<Uuid?>(null)
    private val results = ObservableMutableMap<Uuid, TestCaseValidation>()

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

    private val testPattern = patternStateRepository.flow
        .debounce(DELAY_TYPING)
        .distinctUntilChangedBy { it.text }
        .mapLatest { TestPattern(it.text.value) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TestPattern(
                patternStateRepository.pattern.text.value
            )
        )

    val uiState = combine(
        patternStateRepository.flow,
        testCasesUi,
        testPattern,
    ) { pattern, testCases, testPattern ->
        ValidatorUiState(
            testCases = testCases,
            testPattern = testPattern,
            pattern = pattern.text,
            history = pattern.history,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            testCases = testCasesUi.value,
            testPattern = testPattern.value,
            pattern = patternStateRepository.pattern.text,
            history = patternStateRepository.pattern.history,
        )
    )

    init {
        initialTestCase()
        setupQueueExecution()
        setupPatternListener()
        setupTestCasesListener()
    }

    private fun setupTestCasesListener() = screenModelScope.launch {
        testCasesRepository.flow.collectLatest { testCases ->
            testCases.forEach { testCase ->
                val validation = results[testCase.uuid]

                when {
                    validation == null -> {
                        addToQueue(testCase)
                    }

                    validation.testCase.text != testCase.text -> {
                        addToQueue(testCase, withDelay = true)
                    }

                    validation.testCase.case != testCase.case -> {
                        addToQueue(testCase, withDelay = true)
                    }
                }
            }
        }
    }

    private fun initialTestCase() {
        if (testCasesRepository.all.isEmpty()) {
            TestCase().also { emptyTestCase ->
                testCasesRepository.set(emptyTestCase)
                expanded.value = emptyTestCase.uuid
            }
        }
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

                results[testCase.uuid] = TestCaseValidation(
                    testCase = testCase,
                    result = TestCaseValidation.Result.RUNNING,
                )

                validationJob[testCase.uuid] = launch {
                    results[testCase.uuid] = validateUserCase(
                        testCase = testCase,
                        regex = testPattern.value.regex.getOrThrow()
                    )
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

    private fun addToQueue(
        newTestCase: TestCase,
        withDelay: Boolean = false
    ) = screenModelScope.launch {

        testCaseQueue.dequeue(newTestCase.uuid)

        validationJob[newTestCase.uuid]?.cancelAndJoin()
        validationJob.remove(newTestCase.uuid)

        results[newTestCase.uuid] = TestCaseValidation(newTestCase)

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
                testCasesRepository.set(action.newTestCase)
                expanded.value = action.newTestCase.uuid
            }
        }
    }

    fun onAction(action: TestCaseAction) {
        when (action) {
            is TestCaseAction.ChangeCase -> {
                testCasesRepository.update(action.uuid) {
                    it.copy(
                        case = action.case
                    )
                }
            }

            is TestCaseAction.ChangeText -> {
                testCasesRepository.update(action.uuid) {
                    it.copy(
                        text = action.text
                    )
                }
            }

            is TestCaseAction.ChangeTitle -> {
                testCasesRepository.update(action.uuid) {
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
                patternStateRepository.redo()
            }

            is FooterAction.History.Undo -> {
                patternStateRepository.undo()
            }

            is FooterAction.UpdateRegex -> {
                patternStateRepository.update(action.text)
            }
        }
    }

    companion object {
        private const val DELAY_TYPING = 500L
    }
}
