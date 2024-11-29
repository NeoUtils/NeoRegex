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

interface Syntax {

    val highlight: Highlight

    data class Regex(
        private val modifierColor: Color,
        private val escapeReservedColor: Color,
        private val escapedCharColor: Color,
        private val anchorsColor: Color,
        private val charSetColor: Color,
        private val groupColor: Color,
        private val controlsColor: Color,
    ) : Syntax {

        private val charSetRegex = """(\\{2})|(\\\[)|(\[\^?)((?:\\{2}|\\\]|[^\]])*)(\])?""".toRegex()
        private val groupRegex = """(\\{2})|(\\\[)|(\[(?:\\{2}|\\\]|[^\]])*\]?)|(\\\()|(\((?:\?[:=!])?)((?:\\{2}|\\\)|\\\[|\[(?:\\{2}|\\\]|[^\]])*\]?|\([^\)]*\)|[^\)])*)(\))?""".toRegex()
        private val quantifierRegex = """(\\{2})|(\\\[)|(\[(?:\\{2}|\\\]|[^\]])*\]?)|(\\\{)|(\{\w,?\w?\})""".toRegex()
        private val escapeReservedRegex = """(\\{2})|(\\[{}()\[\]$^+*?|\w])""".toRegex()
        private val escapedCharRegex = """(\\{2})|(\\[DdWwSsHhVvR])""".toRegex()
        private val anchorsRegex = """(\\{2})|(\\[$^])|(\\[AZzBbG])|([$^])""".toRegex()
        private val controlsRegex = """(\\{2})|(\\[tnfrae])""".toRegex()
        private val modifierRegex = """(\\{2})|(\\\[)|(\[(?:\\{2}|\\\]|[^\]])*\]?)|(\\[+*?])|([+*?])""".toRegex()
        private val alternationRegex = """(\\{2})|(\\\[)|(\[(?:\\{2}|\\\]|[^\]])*\]?)|(\\\|)|(\|)""".toRegex()
        private val charSetRangeRegex = """\w(-)\w""".toRegex()

        private fun referenceGroupRegex(number: Int) = """(\\{2})|(\\$number)""".toRegex()

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
                    put(5, groupSpanStyle)
                    put(6, groupSpanStyle.copy(color = Color.Unspecified))
                    put(7, groupSpanStyle)
                }

                charSetRegex.match {

                    // charset
                    put(3, charSetSpanStyle)
                    put(4, charSetSpanStyle.copy(color = Color.Unspecified))
                    put(5, charSetSpanStyle)
                }
            }

            groupRegex.script { match ->
                match.groups.getOrNull(index = 7)?.let {
                    spanStyle {
                        referenceGroupRegex(
                            number = match.index.inc()
                        ).match {
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

        companion object {

            val Default = Regex(
                modifierColor = Color(0xff0077ff),
                escapeReservedColor = Color(0xffb700ff),
                escapedCharColor = Color(0xfff5cd05),
                anchorsColor = Color(0xffb06100),
                charSetColor = Color(0xffe39b00),
                groupColor = Color(0xff038d00),
                controlsColor = Color(0xffff00c9),
            )
        }
    }
}