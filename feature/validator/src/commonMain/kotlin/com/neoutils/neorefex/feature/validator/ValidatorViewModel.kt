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
import com.neoutils.neorefex.feature.validator.model.TestPattern
import com.neoutils.neorefex.feature.validator.state.ValidatorUiState
import com.neoutils.neoregex.core.common.extension.toTextState
import com.neoutils.neoregex.core.common.manager.HistoryManager
import com.neoutils.neoregex.core.common.model.Target
import com.neoutils.neoregex.core.sharedui.component.FooterAction
import com.neoutils.neoregex.core.sharedui.extension.toTextFieldValue
import com.neoutils.neoregex.core.sharedui.model.History
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
    private val patternHistory = HistoryManager()

    private var validationJob = mutableMapOf<Uuid, Job>()
    private var addToQueueJob = mutableMapOf<Uuid, Job>()

    private val testPattern = pattern
        .distinctUntilChangedBy { it.text }
        .map { TestPattern(it.text) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = TestPattern()
        )

    val uiState = combine(
        pattern,
        patternHistory.state,
        testPattern,
        testCases,
        expanded,
    ) { pattern, history, testPattern, testCases, selected ->
        ValidatorUiState(
            pattern = pattern,
            history = History(
                canRedo = history.canRedo,
                canUndo = history.canUndo
            ),
            testCases = testCases,
            expanded = selected,
            error = testPattern.regex.exceptionOrNull()?.message
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ValidatorUiState(
            pattern = pattern.value,
            testCases = testCases.value,
            expanded = expanded.value,
            history = History()
        )
    )

    init {
        setupQueueExecution()
        setupPatternListener()

        pattern.onEach {
            patternHistory.push(it.toTextState())
        }.launchIn(screenModelScope)
    }

    private fun setupPatternListener() = screenModelScope.launch {
        testPattern.collectLatest { testPattern ->

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
            addToQueueJob[Uuid.NIL]?.cancel()

            if (testPattern.isValid) {
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

                    testCases.replace(testCase.uuid) {
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
                expanded.value = action.uuid
            }

            is ValidatorAction.RemoveTestCase -> {
                removeTestCase(action.uuid)
            }

            is ValidatorAction.UpdateTestCase -> {
                updateTestCase(action.newTestCase)
            }

            is ValidatorAction.AddTestCase -> {
                testCases.update { it + action.newTestCase }
                expanded.value = action.newTestCase.uuid
            }
        }
    }

    private fun updateTestCase(
        newTestCase: TestCase,
        testPattern: TestPattern = this.testPattern.value
    ) {
        val oldTestCase = testCases.value.find {
            it.uuid == newTestCase.uuid
        }

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

        testCases.update { testCases ->
            testCases.map { oldTestCase ->
                testCase.takeIf {
                    it.uuid == oldTestCase.uuid
                } ?: oldTestCase
            }
        }

        if (mustValidate && testPattern.isValid) {
            addToQueue(testCase)
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
                val textState = patternHistory.redo() ?: return
                pattern.value = textState.toTextFieldValue()
            }

            is FooterAction.History.Undo -> {
                val textState = patternHistory.undo() ?: return
                pattern.value = textState.toTextFieldValue()
            }

            is FooterAction.UpdateRegex -> {
                patternHistory.unlock()
                pattern.value = action.textState.toTextFieldValue()
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

