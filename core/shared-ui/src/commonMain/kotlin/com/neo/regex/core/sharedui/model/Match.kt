package com.neo.regex.core.sharedui.model

data class Match(
    val text: String,
    val range: IntRange,
    val groups: List<String> = emptyList()
)