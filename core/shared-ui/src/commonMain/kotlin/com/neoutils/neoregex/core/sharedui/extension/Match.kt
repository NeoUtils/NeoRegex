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

package com.neoutils.neoregex.core.sharedui.extension

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.neoutils.neoregex.core.common.model.Match

fun Match.toText(): AnnotatedString {

    val range = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("range: ")
        }
        withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
            append("$range")
        }
    }

    if (groups.isEmpty()) {
        return range
    }

    val groups = buildAnnotatedString {
        groups.mapIndexed { index, group ->

            if (index != 0) {
                append("\n")
            }

            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("$index: ")
            }
            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                append(group)
            }
        }
    }

    val separator = "-".repeat(
        listOf(
            range,
            *groups.split("\n").toTypedArray()
        ).maxBy {
            it.length
        }.length
    )

    return buildAnnotatedString {
        append(range)
        append("\n")
        append(separator)
        append("\n")
        append(groups)
    }
}
