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

package com.neoutils.neoregex.feature.about.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.neoutils.neoregex.core.resources.Res
import com.neoutils.neoregex.core.resources.about_runtime_text
import com.neoutils.neoregex.core.resources.about_vm_text
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun RuntimeInfos(
    modifier: Modifier,
    textStyle: TextStyle
) = Column {

    val vmName = System.getProperty("java.vm.name")
    val vmVersion = System.getProperty("java.vm.version")
    val vmVendor = System.getProperty("java.vm.vendor")

    val mergedTextStyle = typography.bodySmall.merge(textStyle)

    ProvideTextStyle(mergedTextStyle) {
        Text(
            text = stringResource(
                Res.string.about_vm_text,
                vmName,
                vmVendor
            )
        )

        Text(
            text = stringResource(
                Res.string.about_runtime_text,
                vmVersion
            )
        )
    }
}