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

package com.neoutils.neoregex.core.sharedui.event

import com.neoutils.neoregex.core.common.model.Field
import com.neoutils.neoregex.core.common.model.TextState

sealed class FooterAction {
    data class UpdateRegex(
        val text: TextState
    ) : FooterAction()

    sealed class History : FooterAction() {

        abstract val field: Field?

        data class Undo(
            override val field: Field? = null
        ) : History()

        data class Redo(
            override val field: Field? = null
        ) : History()
    }
}