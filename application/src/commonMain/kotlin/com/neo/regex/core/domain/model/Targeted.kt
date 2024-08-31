package com.neo.regex.core.domain.model

class Targeted<T>(
    vararg pairs: Pair<Target, T>
) {

    private val map = pairs.toMap()

    operator fun get(target: Target) = checkNotNull(map[target])
}

fun <T> targeted(
    vararg pairs: Pair<Target, T>
) = Targeted(*pairs)