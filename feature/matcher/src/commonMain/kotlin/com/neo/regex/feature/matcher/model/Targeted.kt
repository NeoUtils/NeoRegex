package com.neo.regex.feature.matcher.model

class Targeted<T>(
    vararg pairs: Pair<Target, T>
) {

    private val map = pairs.toMap()

    operator fun get(target: Target) = checkNotNull(map[target])
}