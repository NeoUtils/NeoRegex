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

package model

import java.io.File

data class Keystore(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String
) {
    constructor(storeFile: File, properties: Map<String, String>) : this(
        storeFile = storeFile,
        storePassword = checkNotNull(properties["STORE_PASSWORD"]),
        keyAlias = checkNotNull(properties["KEY_ALIAS"]),
        keyPassword = checkNotNull(properties["KEY_PASSWORD"])
    )
}