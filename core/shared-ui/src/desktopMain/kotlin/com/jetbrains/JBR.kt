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

package com.jetbrains

import java.lang.invoke.MethodHandles

object JBR {

    private val service by lazy {
        runCatching {
            Class.forName("com.jetbrains.bootstrap.JBRApiBootstrap")
                .getMethod("bootstrap", MethodHandles.Lookup::class.java)
                .invoke(null, MethodHandles.lookup()) as ServiceApi
        }.getOrNull()
    }

    val windowMove by lazy {
        runCatching {
            service?.getService(WindowMove::class.java)
        }.getOrNull()
    }

    interface ServiceApi {
        fun <T> getService(interFace: Class<T>?): T
    }

    object Metadata {
        private val KNOWN_SERVICES = arrayOf(
            "com.jetbrains.JBR\$ServiceApi",
            "com.jetbrains.WindowMove"
        )
        private val KNOWN_PROXIES = emptyArray<String>()
    }
}

