/*
 * NeoRegex.
 *
 * Copyright (C) 2024 Irineu A. Silva.
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

package com.neoutils.neoregex.feature.matcher.action

import com.neoutils.neoregex.core.common.model.Target
import com.neoutils.neoregex.core.common.model.TextState

sealed class MatcherAction {

    data object Toggle : MatcherAction()

    data class TargetChange(
        val target: Target
    ) : MatcherAction()

    data class UpdateText(
        val textState: TextState
    ) : MatcherAction()

    sealed class History : MatcherAction() {

        abstract val textState: Target?

        data class Undo(
            override val textState: Target? = null
        ) : History()

        data class Redo(
            override val textState: Target? = null
        ) : History()
    }
}