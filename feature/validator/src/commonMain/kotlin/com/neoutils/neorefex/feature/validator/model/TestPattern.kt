package com.neoutils.neorefex.feature.validator.model

data class TestPattern(
    val pattern: String = ""
) {
    val regex = runCatching { Regex(pattern) }
    val isValid = regex.isSuccess && pattern.isNotEmpty()
    val isInvalid = regex.isFailure || pattern.isEmpty()
}