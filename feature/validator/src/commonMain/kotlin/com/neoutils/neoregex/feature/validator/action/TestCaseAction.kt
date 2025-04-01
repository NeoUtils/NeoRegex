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

package com.neoutils.neoregex.feature.validator.action

import com.neoutils.neoregex.core.common.model.TestCase
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed class TestCaseAction {
    data class ChangeTitle(
        val uuid: Uuid,
        val title: String
    ) : TestCaseAction()

    data class ChangeText(
        val uuid: Uuid,
        val text: String
    ) : TestCaseAction()

    data class ChangeCase(
        val uuid: Uuid,
        val case: TestCase.Case
    ) : TestCaseAction()

    data class Delete(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Collapse(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Expanded(
        val uuid: Uuid,
    ) : TestCaseAction()

    data class Duplicate(
        val uuid: Uuid,
    ) : TestCaseAction()
}