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
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
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
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.fontSizes
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.match_result_infos
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Duration
import kotlin.time.DurationUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun BoxScope.MatchesInfos(
    duration: Duration,
    matches: Int,
    modifier: Modifier
) {

    val density = LocalDensity.current

    var isRunning by remember { mutableStateOf(false) }

    val animateOffset = remember {
        Animatable(
            Offset.Zero,
            Offset.VectorConverter,
        )
    }

    var targetSize by remember { mutableStateOf(DpSize.Zero) }

    var targetRect by remember { mutableStateOf(Rect.Zero) }

    var rectMap by remember { mutableStateOf<Map<Alignment, Rect>>(mapOf()) }

    var currentAlignment by remember { mutableStateOf(Alignment.BottomEnd) }

    val scope = rememberCoroutineScope()

    listOf(
        Alignment.TopEnd,
        Alignment.BottomEnd
    ).forEach { alignment ->
        AnimatedVisibility(
            visible = isRunning,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(alignment)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = colorScheme.primary.copy(
                            alpha = 0.2f
                        ),
                        shape = RoundedCornerShape(dimensions.tiny)
                    )
                    .run {
                        rectMap[alignment]?.takeIf {
                            !it.intersect(targetRect).isEmpty
                        }?.let {
                            border(
                                width = 1.dp,
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(dimensions.tiny)
                            )
                        } ?: this
                    }
                    .onGloballyPositioned {
                        rectMap = rectMap + mapOf(
                            alignment to it.boundsInRoot()
                        )
                    }
                    .size(targetSize)
            )
        }
    }

    val hover = remember { MutableInteractionSource() }

    Text(
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
            .align(currentAlignment)
            .offset { animateOffset.value.round() }
            .padding(dimensions.tiny) // external
            .background(
                color = colorScheme.surfaceVariant,
                shape = RoundedCornerShape(dimensions.tiny)
            )
            .onGloballyPositioned {
                targetSize = density.run { it.size.toSize().toDpSize() }
                targetRect = it.boundsInRoot()
            }
            .hoverable(hover)
            .indication(
                interactionSource = hover,
                indication = ripple()
            )
            .onDrag(
                onDragStart = {
                    isRunning = true
                },
                onDragEnd = {
                    rectMap.entries.find {
                        !it.value.intersect(targetRect).isEmpty
                    }?.let {
                        scope.launch {

                            currentAlignment = it.key

                            animateOffset.snapTo(
                                targetValue = targetRect.topLeft - it.value.topLeft
                            )

                            animateOffset.animateTo(Offset.Zero)
                        }
                    } ?: run {
                        scope.launch {
                            animateOffset.animateTo(Offset.Zero)
                        }
                    }

                    isRunning = false
                },
                onDrag = { dragAmount ->
                    scope.launch {
                        animateOffset.snapTo(
                            targetValue = animateOffset.value + dragAmount
                        )
                    }
                },
                onDragCancel = {
                    scope.launch {
                        animateOffset.animateTo(Offset.Zero)
                    }

                    isRunning = false
                }
            )
            .padding(dimensions.micro) // internal
    )
}
