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

package com.neoutils.neoregex.feature.matcher.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.neoutils.highlight.compose.extension.spanStyle
import com.neoutils.highlight.core.Highlight
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.xregex.Flavor
import com.neoutils.xregex.extension.isSupported
import com.neoutils.xregex.extension.xRegex

interface Syntax {

    val highlight: Highlight

    data class Regex(
        private val modifierColor: Color = Color(color = 0xff0077ff),
        private val escapeReservedColor: Color = Color(color = 0xffb700ff),
        private val escapedCharColor: Color = Color(color = 0xfff5cd05),
        private val anchorsColor: Color = Color(color = 0xffb06100),
        private val charSetColor: Color = Color(color = 0xffe39b00),
        private val groupColor: Color = Color(color = 0xff038d00),
        private val controlsColor: Color = Color(color = 0xffff00c9),
    ) : Syntax {

        private val charSetRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[\^?)((?:\\{2}|\\\]|[^\]])*)(\])?"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val groupRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[(?:\\{2}|\\\]|[^\]])*\]?)""",
            """(\\\()""",
            """((\((?:\?[:=!])?)((?:\\{2}|\\\)|\\\[|\[(?:\\{2}|\\\]|[^\]])*\]?|[^\)])*)(\))?)"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val quantifierRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[(?:\\{2}|\\\]|[^\]])*\]?)""",
            """(\\\{)|(\{\w,?\w?\})"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val escapeReservedRegex = listOf(
            """(\\{2})""",
            """(\\[{}()\[\]${'$'}^+*?|.\w])"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val escapedCharRegex = listOf(
            """(\\{2})""",
            """(\\[DdWwSsHhVvR])"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val anchorsRegex = listOf(
            """(\\{2})""",
            """(\\[$^])""",
            """(\\[AZzBbG])""",
            """([$^])"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val controlsRegex = listOf(
            """(\\{2})""",
            """(\\[tnfrae])"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val modifierRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[(?:\\{2}|\\\]|[^\]])*\]?)""",
            """(\\[+*?])""",
            """([+*?])"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val dotRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[(?:\\{2}|\\\]|[^\]])*\]?)""",
            """(\\\.)""",
            """(\.)"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val alternationRegex = listOf(
            """(\\{2})""",
            """(\\\[)""",
            """(\[(?:\\{2}|\\\]|[^\]])*\]?)""",
            """(\\\|)""",
            """(\|)"""
        ).joinToString(
            separator = "|"
        ).toRegex()

        private val charSetRangeRegex = """\w(-)\w""".toRegex()

        private val Int.referenceGroupRegex
            get() = listOf(
                """(\\{2})""",
                """(\\$this)"""
            ).joinToString(
                separator = "|"
            ).xRegex()

        private val charSetSpanStyle = SpanStyle(
            color = charSetColor,
            background = charSetColor.copy(
                alpha = 0.2f
            )
        )

        private val groupSpanStyle = SpanStyle(
            color = groupColor,
            background = groupColor.copy(
                alpha = 0.2f
            )
        )

        override val highlight = Highlight {
            spanStyle {

                escapeReservedRegex.match {

                    // full match
                    put(0, SpanStyle(color = escapeReservedColor))
                }

                modifierRegex.match {

                    // modifier
                    put(5, SpanStyle(color = modifierColor))
                }

                quantifierRegex.match {

                    // quantifier
                    put(5, SpanStyle(color = modifierColor))
                }

                alternationRegex.match {

                    // alternation
                    put(5, SpanStyle(color = groupColor))
                }

                controlsRegex.match {

                    // controls
                    put(2, SpanStyle(color = controlsColor))
                }

                escapedCharRegex.match {

                    // escaped char
                    put(2, SpanStyle(color = escapedCharColor))
                }

                anchorsRegex.match {

                    // anchors
                    put(3, SpanStyle(color = anchorsColor))
                    put(4, SpanStyle(color = anchorsColor))
                }

                groupRegex.match {

                    // charset
                    put(6, groupSpanStyle)
                    put(7, groupSpanStyle.copy(color = Color.Unspecified))
                    put(8, groupSpanStyle)
                }

                charSetRegex.match {

                    // charset
                    put(3, charSetSpanStyle)
                    put(4, charSetSpanStyle.copy(color = Color.Unspecified))
                    put(5, charSetSpanStyle)
                }

                dotRegex.match {
                    put(5, SpanStyle(color = escapedCharColor))
                }
            }

            groupRegex.script { match ->
                match.groups.getOrNull(index = 8)?.let {
                    spanStyle {
                        match.index.inc().referenceGroupRegex.match {
                            put(2, SpanStyle(color = groupColor))
                        }
                    }
                }
            }

            charSetRegex.script { match ->
                match.groups.getOrNull(index = 4)?.let { group ->
                    spanStyle {
                        charSetRangeRegex.groups(
                            SpanStyle(color = charSetColor),
                            range = group.range
                        )
                    }
                }
            }
        }
    }
}