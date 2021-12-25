package com.neo.regex.model

import java.util.regex.Pattern

data class Expression(
    var regex: String = "",
    var pattern: Pattern? = null
) {
}