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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.datasource.PreferencesDataSource
import com.neoutils.neoregex.core.datasource.model.Preferences
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.fontSizes
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.match_result_infos
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import org.koin.compose.koinInject
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Performance(
    val duration: Duration = Duration.ZERO,
    val matches: Int = 0
)

@Composable
fun BoxWithConstraintsScope.Performance(
    performance: Performance,
    textStyle: TextStyle = TextStyle(),
    preferencesDataSource: PreferencesDataSource = koinInject()
) {

    val preferences by preferencesDataSource.flow.collectAsStateWithLifecycle()

    val current = rememberUpdatedState(
        when (preferences.performanceLabelAlign) {
            Preferences.Alignment.TOP_END -> Alignment.TopEnd
            Preferences.Alignment.BOTTOM_END -> Alignment.BottomEnd
        }
    )

    val density = LocalDensity.current

    var isRunning by remember { mutableStateOf(false) }

    val animateOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    var alignments by remember { mutableStateOf<Map<Alignment, Rect>>(mapOf()) }

    // It needs to be a state to update the reference in pointerInput()
    val halfHeight by rememberUpdatedState(density.run { maxHeight.toPx() / 2f })

    var targetRect by remember { mutableStateOf(Rect.Zero) }

    val destination = remember { mutableStateOf<Alignment?>(null) }

    val scope = rememberCoroutineScope()

    val mergedTextStyle = typography.labelSmall.merge(textStyle)

    listOf(
        Alignment.TopEnd,
        Alignment.BottomEnd
    ).forEach { alignment ->
        AlignmentTarget(
            alignment = alignment,
            isVisible = isRunning,
            isTarget = alignment == destination.value,
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

    val hover = remember { MutableInteractionSource() }

    Text(
        text = pluralStringResource(
            Res.plurals.match_result_infos,
            performance.matches,
            performance.matches,
            performance.duration.toString(
                unit = DurationUnit.MILLISECONDS,
                decimals = 3
            )
        ),
        style = mergedTextStyle,
        modifier = Modifier
            .align(current.value)
            .offset { animateOffset.value.round() }
            .padding(dimensions.tiny) // external
            .background(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(dimensions.tiny)
            )
            .clip(shape = RoundedCornerShape(dimensions.tiny))
            .hoverable(hover)
            .indication(
                interactionSource = hover,
                indication = ripple()
            )
            .onGloballyPositioned {
                targetRect = it.boundsInParent()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isRunning = true
                    },
                    onDragEnd = {
                        scope.launch {
                            destination.value?.let { destination ->
                                alignments[destination]?.let {
                                    animateOffset.snapTo(
                                        targetValue = targetRect.topLeft - it.topLeft
                                    )
                                }

                                preferencesDataSource.update {
                                    it.copy(
                                        performanceLabelAlign = when (destination) {
                                            Alignment.TopEnd -> Preferences.Alignment.TOP_END
                                            Alignment.BottomEnd -> Preferences.Alignment.BOTTOM_END
                                            else -> error("Invalid alignment $destination")
                                        }
                                    )
                                }
                            }

                            animateOffset.animateTo(Offset.Zero)

                            destination.value = null
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

                        destination.value = when {
                            current.value == Alignment.TopEnd &&
                                    targetRect.center.y > halfHeight -> Alignment.BottomEnd

                            current.value == Alignment.BottomEnd &&
                                    targetRect.center.y < halfHeight -> Alignment.TopEnd

                            else -> current.value
                        }
                    },
                    onDragCancel = {
                        scope.launch {
                            animateOffset.animateTo(Offset.Zero)
                        }

                        destination.value = null
                        isRunning = false
                    }
                )
            }
            .padding(
                vertical = dimensions.micro,
                horizontal = dimensions.tiny
            ) // internal
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
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(dimensions.tiny)
            ).run {
                if (isTarget) {
                    border(
                        width = 1.dp,
                        color = colorScheme.outline,
                        shape = RoundedCornerShape(dimensions.tiny)
                    )
                } else this
            }
    )
}