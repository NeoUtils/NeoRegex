package com.neo.regex.core.sharedui.model

data class Match(
    val text: String,
    val range: IntRange,
    val groups: List<String> = emptyList()
) {
    override fun toString(): String {

        val range = "range: $range"

        if (groups.isEmpty()) {
            return range
        }

        val groups = groups.mapIndexed { index, group ->
            "$index: $group"
        }

        val separator = "─".repeat(
            listOf(
                range,
                *groups.toTypedArray()
            ).maxBy {
                it.length
            }.length
        )

        return listOf(
            range,
            separator,
            *groups.toTypedArray()
        ).joinToString("\n")
    }
}