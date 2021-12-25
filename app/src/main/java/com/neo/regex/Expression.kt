package com.neo.regex

import java.util.regex.Pattern

data class Expression(
    var regex: String = "",
    var pattern: Pattern? = null
) {
}