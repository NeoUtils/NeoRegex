/*
 * NeoRegex.
 *
 * Copyright (C) 2025 Irineu A. Silva.
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
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.common.platform.Platform
import com.neoutils.neoregex.core.common.platform.platform
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.manager.salvage.SalvageManager
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.app_name
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun NeoTitle(
    modifier: Modifier = Modifier,
    salvageManager: SalvageManager = koinInject(),
    titleStyle: TextStyle = TextStyle()
) = AnimatedContent(
    modifier = modifier,
    targetState = salvageManager
        .flow
        .collectAsStateWithLifecycle(
            initialValue = null
        ).value,
    contentKey = { it != null },
    transitionSpec = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Down
        ) togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Down
        )
    },
    contentAlignment = Alignment.Center,
) { salvage ->
    if (salvage == null) {
        Text(
            text = stringResource(Res.string.app_name),
            style = typography.titleSmall.copy(
                fontFamily = null
            ).merge(titleStyle)
        )
    } else {
        val coroutine = rememberCoroutineScope()

        SalvageUi(
            modifier = Modifier.height(
                when (platform) {
                    Platform.Android -> 30.dp
                    else -> dimensions.big
                }
            ),
            opened = salvage,
            onAction = { action ->
                when (action) {
                    SalvageAction.Close -> {
                        coroutine.launch {
                            salvageManager.close()
                        }
                    }

                    SalvageAction.Update -> {
                        coroutine.launch {
                            salvageManager.update()
                        }
                    }

                    is SalvageAction.ChangeName -> {
                        coroutine.launch {
                            salvageManager.update {
                                it.copy(
                                    title = action.name
                                )
                            }
                        }
                    }

                    SalvageAction.Reset -> {
                        coroutine.launch {
                            salvageManager.sync()
                        }
                    }
                }
            }
        )
    }
}