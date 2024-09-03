package com.neo.regex.core.sharedui.model

data class Match(
    val number: Int,
    val text: String,
    val range: IntRange,
    val groups: List<String> = emptyList()
)