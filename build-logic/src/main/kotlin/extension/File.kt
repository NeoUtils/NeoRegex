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

package extension

import model.Environment
import model.Keystore
import java.io.File
import java.util.*

fun File.properties(): Map<String, String>? {

    if (!exists()) return null

    val properties = Properties().apply {
        inputStream().use { load(it) }
    }

    return buildMap {
        properties.forEach {
            put(it.key.toString(), it.value.toString())
        }
    }
}

fun File.keystore(): Keystore? {

    val properties = properties() ?: return null

    return Keystore(
        storeFile = File(parent, checkNotNull(properties["STORE_FILE"])),
        properties = properties
    )
}

fun File.environment(): Environment? {

    val properties = properties() ?: return null

    return Environment(properties)
}