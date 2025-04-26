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

package model

data class Environment(
    val bugsnagAndroidApiKey: String = "",
    val bugsnagDesktopApiKey: String = "",
    val bugsnagWebApikey: String = ""
) {
    constructor(
        properties: Map<String, String>
    ) : this(
        bugsnagAndroidApiKey = properties.getOrDefault("BUGSNAG_ANDROID_API_KEY", ""),
        bugsnagDesktopApiKey = properties.getOrDefault("BUGSNAG_DESKTOP_API_KEY", ""),
        bugsnagWebApikey = properties.getOrDefault("BUGSNAG_WEB_API_KEY", "")
    )
}