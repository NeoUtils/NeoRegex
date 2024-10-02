/*
 * NeoRegex.
 *
 * Copyright (C) 2024 <AUTHOR>.
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

package com.neoutils.neoregex.core.common.util

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import com.neoutils.neoregex.core.common.util.UiMode

fun UiMode.Companion.resolve(context: Context): UiMode {

    return when (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
        UI_MODE_NIGHT_YES -> {
            UiMode.DARK
        }

        else -> {
            UiMode.LIGHT
        }
    }
}
