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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme.dimensions
import com.neoutils.neoregex.core.dispatcher.NavigationDispatcher
import com.neoutils.neoregex.core.dispatcher.event.Navigation
import com.neoutils.neoregex.core.sharedui.extension.name
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navigation: NavigationDispatcher = koinInject(),
    textStyle: TextStyle = TextStyle()
) = Column(
    modifier = modifier
) {
    val expanded = remember { mutableStateOf(false) }

    val mergedTextStyle = typography.labelLarge.copy(
        fontFamily = null
    ).merge(textStyle)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(dimensions.tiny))
            .clickable { expanded.value = true }
            .padding(dimensions.tiny),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val current by navigation.current.collectAsStateWithLifecycle()

        Text(
            text = current.name,
            style = mergedTextStyle
        )

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowDown,
            modifier = Modifier.size(18.dp),
            contentDescription = null
        )
    }

    val coroutine = rememberCoroutineScope()

    DropdownMenu(
        expanded = expanded.value,
        onDismissRequest = { expanded.value = false }
    ) {
        listOf(
            Navigation.Matcher,
            Navigation.About,
        ).forEach {
            DropdownMenuItem(
                text = { Text(it.name) },
                onClick = {
                    coroutine.launch {
                        navigation.emit(it)
                        expanded.value = false
                    }
                },
            )
        }
    }
}
