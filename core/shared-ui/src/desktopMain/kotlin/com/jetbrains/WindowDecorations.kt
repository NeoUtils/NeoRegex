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

package com.jetbrains

import java.awt.Dialog
import java.awt.Frame
import java.awt.Window

interface WindowDecorations {

    fun setCustomTitleBar(frame: Frame?, customTitleBar: CustomTitleBar?)

    fun setCustomTitleBar(dialog: Dialog?, customTitleBar: CustomTitleBar?)

    fun createCustomTitleBar(): CustomTitleBar?

    interface CustomTitleBar {

        var height: Float

        val properties: Map<String?, Any?>?

        fun putProperties(m: Map<String?, *>?)

        fun putProperty(key: String?, value: Any?)

        val leftInset: Float

        val rightInset: Float

        fun forceHitTest(client: Boolean)

        val containingWindow: Window?
    }
}