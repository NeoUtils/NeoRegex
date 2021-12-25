package com.neo.regex

import java.util.regex.Pattern

data class ExpressionState(
    var regex: String = "",
    var hsv: Int? = null,
    var pattern: Pattern? = null
) {
}