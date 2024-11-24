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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.fontSizes
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.match_result_infos
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Composable
fun BoxWithConstraintsScope.MatchesInfos(infos: MatchesInfos) {

    val density = LocalDensity.current

    var isRunning by remember { mutableStateOf(false) }

    val animateOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var alignments by remember { mutableStateOf<Map<Alignment, Rect>>(mapOf()) }

    var current by remember { mutableStateOf(Alignment.BottomEnd) }

    // It needs to be a state to update the reference in pointerInput()
    val halfHeight by rememberUpdatedState(density.run { maxHeight.toPx() / 2f })

    var targetRect by remember { mutableStateOf(Rect.Zero) }

    var destination by remember { mutableStateOf(current) }

    val scope = rememberCoroutineScope()

    listOf(
        Alignment.TopEnd,
        Alignment.BottomEnd
    ).forEach { alignment ->
        AlignmentTarget(
            alignment = alignment,
            isVisible = isRunning,
            isTarget = alignment == destination,
            modifier = Modifier
                .padding(dimensions.tiny)
                .size(density.run { targetRect.size.toDpSize() })
                .onGloballyPositioned {
                    alignments = alignments + mapOf(
                        alignment to it.boundsInParent()
                    )
                }
        )
    }

    Text(
        text = pluralStringResource(
            Res.plurals.match_result_infos,
            infos.matches,
            infos.matches,
            infos.duration.toString(
                unit = DurationUnit.MILLISECONDS,
                decimals = 3
            )
        ),
        fontSize = fontSizes.tiny,
        style = typography.labelSmall,
        modifier = Modifier
            .align(current)
            .offset { animateOffset.value.round() }
            .padding(dimensions.tiny) // external
            .background(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(dimensions.tiny)
            )
            .onGloballyPositioned {
                targetRect = it.boundsInParent()
            }
            .run {
                val hover = remember { MutableInteractionSource() }

                hoverable(hover)
                    .indication(
                        interactionSource = hover,
                        indication = ripple()
                    )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isRunning = true
                    },
                    onDragEnd = {
                        scope.launch {

                            alignments[destination]?.let {
                                animateOffset.snapTo(
                                    targetValue = targetRect.topLeft - it.topLeft
                                )
                            }

                            current = destination

                            animateOffset.animateTo(Offset.Zero)
                        }

                        isRunning = false
                    },
                    onDrag = { changes, dragAmount ->
                        changes.consume()

                        scope.launch {
                            animateOffset.snapTo(
                                targetValue = animateOffset.value + dragAmount
                            )
                        }

                        destination = when {
                            current == Alignment.TopEnd &&
                                    targetRect.center.y > halfHeight -> Alignment.BottomEnd

                            current == Alignment.BottomEnd &&
                                    targetRect.center.y < halfHeight -> Alignment.TopEnd

                            else -> current
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            animateOffset.animateTo(Offset.Zero)
                        }

                        isRunning = false
                    }
                )
            }
            .padding(dimensions.micro) // internal
    )
}

@Composable
private fun BoxScope.AlignmentTarget(
    isVisible: Boolean,
    alignment: Alignment,
    isTarget: Boolean,
    modifier: Modifier = Modifier
) = AnimatedVisibility(
    visible = isVisible,
    enter = fadeIn(),
    exit = fadeOut(),
    modifier = modifier.align(alignment)
) {
    Box(
        modifier = Modifier
            .background(
                color = colorScheme.primary.copy(
                    alpha = 0.2f
                ),
                shape = RoundedCornerShape(dimensions.tiny)
            ).run {
                if (isTarget) {
                    border(
                        width = 1.dp,
                        color = colorScheme.primary,
                        shape = RoundedCornerShape(dimensions.tiny)
                    )
                } else this
            }
    )
}

data class MatchesInfos(
    val duration: Duration,
    val matches: Int
) {
    companion object {
        fun create(
            duration: Duration = Duration.ZERO,
            matches: Int = 0
        ): MatchesInfos? = when (platform) {
            is Platform.Android,
            is Platform.Desktop -> {
                MatchesInfos(
                    duration = duration,
                    matches = matches
                )
            }

            Platform.Web -> null
        }
    }
}