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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.fontSizes
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.match_result_infos
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Composable
actual fun BoxScope.MatchesResult(
    duration: Duration,
    matches: Int,
    modifier: Modifier
) = Text(
    text = pluralStringResource(
        Res.plurals.match_result_infos,
        matches, matches,
        duration.toString(
            unit = DurationUnit.MILLISECONDS,
            decimals = 3
        )
    ),
    fontSize = fontSizes.tiny,
    style = typography.labelSmall,
    modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(4.dp)
        .background(
            color = colorScheme.surfaceVariant,
            shape = RoundedCornerShape(dimensions.tiny)
        )
        .padding(2.dp)
)