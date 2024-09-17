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

package com.neo.regex.core.common.platform

enum class DesktopOS {
    WINDOWS,
    MAC_OS,
    LINUX;

    companion object {
        val Current: DesktopOS by lazy {
            val name = System.getProperty("os.name")
            when {
                name?.startsWith("Linux") == true -> LINUX
                name?.startsWith("Win") == true -> WINDOWS
                name?.startsWith("Mac") == true -> MAC_OS
                else -> error("Unsupported desktop platform: $name")
            }
        }
    }
}