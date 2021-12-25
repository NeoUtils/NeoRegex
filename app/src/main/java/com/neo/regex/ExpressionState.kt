package com.neo.regex

import java.util.regex.Pattern

data class ExpressionState(
    var regex: String = "",
    var pattern: Pattern? = null
) {
}