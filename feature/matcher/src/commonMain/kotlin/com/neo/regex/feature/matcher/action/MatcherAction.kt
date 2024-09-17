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

package com.neo.regex.feature.matcher.action

import com.neo.regex.feature.matcher.model.Target
import com.neo.regex.feature.matcher.model.TextState

sealed class MatcherAction {

    data object Toggle : MatcherAction()

    data class TargetChange(
        val target: Target
    ) : MatcherAction()

    sealed class Input : MatcherAction() {

        abstract val textState: TextState

        data class UpdateText(
            override val textState: TextState
        ) : Input()

        data class UpdateRegex(
            override val textState: TextState
        ) : Input()
    }

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