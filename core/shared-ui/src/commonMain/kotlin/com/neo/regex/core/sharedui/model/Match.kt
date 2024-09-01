package com.neo.regex.core.sharedui.model

data class Match(
    val start: Int,
    val end: Int
) {
    override fun toString(): String {
        return "range: $start - ${end - 1}"
    }
}