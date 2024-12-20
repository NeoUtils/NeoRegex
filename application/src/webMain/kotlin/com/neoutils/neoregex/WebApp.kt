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

package com.neoutils.neoregex

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoutils.neoregex.core.common.extension.toCss
import com.neoutils.neoregex.core.designsystem.theme.NeoTheme
import com.neoutils.neoregex.core.sharedui.di.WithKoin
import kotlinx.browser.document

@Composable
fun WebApp() = WithKoin {
    NeoTheme {

        val background = colorScheme.background.toCss()

        LaunchedEffect(Unit) {
            val body = checkNotNull(document.body)
            body.style.backgroundColor = background
        }

        Experimental {
            App()
        }
    }
}

@Composable
fun Experimental(
    size: DpSize = DpSize(250.dp, 250.dp),
    content: @Composable () -> Unit
) = Box {

    content()

    val density = LocalDensity.current

    val translation = density.run {
        Offset(
            x = (size.width / 3.5f).toPx(),
            y = (size.height / 3.5f).toPx().unaryMinus()
        )
    }

    Box(
        modifier = Modifier
            .size(size)
            .align(Alignment.TopEnd)
            .graphicsLayer(
                translationX = translation.x,
                translationY = translation.y,
                rotationZ = 45f
            )
    ) {
        Text(
            text = "experimental",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .background(Color.Yellow)
                .padding(vertical = 8.dp),
        )
    }
}

