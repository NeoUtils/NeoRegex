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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp
import com.neoutils.neoregex.core.common.extension.getBoundingBoxes
import com.neoutils.neoregex.core.common.extension.toText
import com.neoutils.neoregex.core.common.extension.toTextFieldValue
import com.neoutils.neoregex.core.common.model.DrawMatch
import com.neoutils.neoregex.core.common.model.Match
import com.neoutils.neoregex.core.common.model.TextState
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions

@Composable
actual fun TextEditor(
    value: TextState,
    onValueChange: (TextState) -> Unit,
    modifier: Modifier,
    onFocusChange: (FocusState) -> Unit,
    matches: List<Match>,
    textStyle: TextStyle,
    config: Config
) = Column(modifier) {

    val mergedTextStyle = typography.bodyMedium.copy(
        letterSpacing = 1.sp,
    ).merge(textStyle)

    val scrollState = rememberScrollState()

    var textLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    val interactionSource = remember { MutableInteractionSource() }

    var pressedMatchOffset by remember { mutableStateOf<Offset?>(null) }

    var selectedMatch by remember { mutableStateOf<Match?>(null) }

    LaunchedEffect(interactionSource, matches) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    pressedMatchOffset = interaction.pressPosition
                }

                is PressInteraction.Release -> {
                    selectedMatch = textLayout?.let { textLayout ->
                        matches.firstOrNull { match ->
                            textLayout
                                .getBoundingBoxes(
                                    match.range.first,
                                    match.range.last
                                )
                                .any {
                                    it.contains(interaction.press.pressPosition)
                                }
                        }
                    }

                    pressedMatchOffset = null
                }

                is PressInteraction.Cancel -> {
                    pressedMatchOffset = null
                }
            }
        }
    }

    LaunchedEffect(matches, selectedMatch) {
        selectedMatch = matches.firstOrNull { match ->
            selectedMatch == match
        }
    }

    Row(Modifier.weight(weight = 1f, fill = true)) {
        LineNumbers(
            count = textLayout?.lineCount ?: 1,
            offset = scrollState.value,
            textStyle = TextStyle(
                lineHeight = mergedTextStyle.lineHeight,
                fontSize = mergedTextStyle.fontSize,
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier
                .background(colorScheme.surfaceVariant)
                .fillMaxHeight()
        )

        val textFileValue = remember(value) { value.toTextFieldValue() }

        // TODO(improve): it's not performant for large text
        BasicTextField(
            value = textFileValue,
            onValueChange = {
                onValueChange(it.toText())
            },
            textStyle = mergedTextStyle.copy(
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Proportional,
                    trim = LineHeightStyle.Trim.None
                ),
                color = colorScheme.onSurface,
            ),
            interactionSource = interactionSource,
            cursorBrush = SolidColor(colorScheme.onSurface),
            modifier = Modifier
                .background(colorScheme.surface)
                .padding(start = dimensions.nano.m)
                .fillMaxSize()
                .verticalScroll(scrollState) // TODO(improve): https://github.com/NeoUtils/NeoRegex/issues/15
                .onFocusChanged(onFocusChange)
                .drawBehind {
                    val drawMatches = textLayout
                        ?.let { textLayout ->
                            matches.map { match ->
                                DrawMatch(
                                    match = match,
                                    rects = textLayout
                                        .getBoundingBoxes(
                                            match.range.first,
                                            match.range.last
                                        )
                                        .map {
                                            it.deflate(
                                                delta = 0.8f
                                            )
                                        }
                                )

                            }
                        }
                        .orEmpty()

                    drawMatches.forEach { (_, rects) ->
                        rects.forEach { rect ->
                            drawRect(
                                color = config.matchColor,
                                topLeft = Offset(rect.left, rect.top),
                                size = Size(rect.width, rect.height)
                            )
                        }
                    }

                    val drawMatch = drawMatches.firstOrNull { (match, rects) ->
                        pressedMatchOffset?.let { offset ->
                            rects.any { it.contains(offset) }
                        } ?: run {
                            selectedMatch == match
                        }
                    }

                    drawMatch?.let { (_, rects) ->
                        rects.forEach { rect ->
                            drawRect(
                                color = config.selectedMatchColor,
                                topLeft = Offset(
                                    x = rect.left,
                                    y = rect.top - scrollState.value
                                ),
                                size = Size(rect.width, rect.height),
                                style = Stroke(
                                    width = 1f
                                )
                            )
                        }
                    }
                },
            onTextLayout = {
                textLayout = it
            }
        )
    }

    AnimatedContent(
        targetState = selectedMatch,
        label = "animated_match_interaction",
        transitionSpec = {
            val showUp = fadeIn() + slideIntoContainer(SlideDirection.Up)
            val hideDown = fadeOut() + slideOutOfContainer(SlideDirection.Down)

            showUp togetherWith hideDown
        },
        contentKey = { it != null }
    ) { match ->
        if (match != null) {
            MatchDetails(
                match = match,
                modifier = Modifier.fillMaxWidth(),
                textStyle = mergedTextStyle
            )
        } else {
            Spacer(Modifier.fillMaxWidth())
        }
    }
}
