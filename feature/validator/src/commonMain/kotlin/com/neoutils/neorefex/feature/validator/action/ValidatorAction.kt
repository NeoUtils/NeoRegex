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

package com.neoutils.neorefex.feature.validator.action

import com.neoutils.neorefex.feature.validator.model.TestCase
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
sealed class ValidatorAction {

    data class UpdateTestCase(
        val newTestCase: TestCase
    ) : ValidatorAction()

    data class ExpandedTestCase(
        val targetUuid: Uuid
    ) : ValidatorAction()

    data class CollapseTestCase(
        val targetUuid: Uuid
    ) : ValidatorAction()

    data class RemoveTestCase(
        val targetUuid: Uuid
    ) : ValidatorAction()

    data class AddTestCase(
        val newTestCase: TestCase = TestCase()
    ) : ValidatorAction()

    data class Duplicate(
        val targetUuid: Uuid
    ) : ValidatorAction()
}