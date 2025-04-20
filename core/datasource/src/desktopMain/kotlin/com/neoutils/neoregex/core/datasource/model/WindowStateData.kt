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

package com.neoutils.neoregex.core.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class WindowStateData(
    val position: Position?,
    val size: Size,
    val placement: Placement
) {

    @Serializable
    data class Position(
        val x: Int,
        val y: Int
    )

    @Serializable
    data class Size(
        val width: Int,
        val height: Int
    )

    @Serializable
    enum class Placement {
        FLOATING,
        MAXIMIZED,
        FULLSCREEN
    }

    companion object {
        val Default = WindowStateData(
            position = null,
            size = Size(
                width = 640,
                height = 480
            ),
            placement = Placement.FLOATING
        )
    }
}