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

package com.neoutils.neoregex.core.common.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ObservableMutableMap<K, T>(
    private val map: MutableMap<K, T> = mutableMapOf()
) : MutableMap<K, T> {

    constructor(
        vararg pairs: Pair<K, T>
    ) : this(mutableMapOf(*pairs))

    private val _valuesFlow = MutableStateFlow(values.toList())
    val valuesFlow = _valuesFlow.asStateFlow()

    private val _mapFlow = MutableStateFlow(map.toMap())
    val mapFlow = _mapFlow.asStateFlow()

    override val entries get() = map.entries
    override val keys get() = map.keys
    override val size get() = map.size
    override val values get() = map.values

    override fun containsKey(key: K) = map.containsKey(key)

    override fun containsValue(value: T) = map.containsValue(value)

    override fun get(key: K) = map[key]

    override fun isEmpty() = map.isEmpty()

    override fun clear() = map.clear().also {
        _valuesFlow.value = values.toList()
        _mapFlow.value = map.toMap()
    }

    override fun remove(key: K) = map.remove(key).also {
        _valuesFlow.value = values.toList()
        _mapFlow.value = map.toMap()
    }

    override fun putAll(from: Map<out K, T>) = map.putAll(from).also {
        _valuesFlow.value = values.toList()
        _mapFlow.value = map.toMap()
    }

    override fun put(key: K, value: T) = map.put(key, value).also {
        _valuesFlow.value = values.toList()
        _mapFlow.value = map.toMap()
    }
}