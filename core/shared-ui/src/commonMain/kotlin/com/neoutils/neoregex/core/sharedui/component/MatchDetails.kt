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

package com.neoutils.neoregex.core.sharedui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.common.model.Match

@Composable
fun MatchDetails(
    match: Match,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle()
) = Surface(
    modifier = modifier,
    shape = RectangleShape,
    shadowElevation = dimensions.small
) {

    val mergedTextStyle = typography.bodyLarge.merge(textStyle)

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
    ) {

        Column(
            modifier = Modifier
                .padding(dimensions.default)
                .weight(weight = 1f)
        ) {
            Text(
                text = "match ${match.number}",
                style = mergedTextStyle.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            HorizontalDivider(
                modifier = Modifier.padding(
                    vertical = dimensions.small,
                )
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("range: ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(match.range.toString())
                    }
                },
                style = mergedTextStyle
            )
        }

        if (match.groups.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .padding(dimensions.default)
                    .weight(weight = 1f)
            ) {
                Text(
                    text = "groups",
                    style = mergedTextStyle.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = dimensions.small,
                    )
                )

                match.groups.forEachIndexed { index, group ->
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$index: ")
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                                append(group)
                            }
                        },
                        style = mergedTextStyle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
