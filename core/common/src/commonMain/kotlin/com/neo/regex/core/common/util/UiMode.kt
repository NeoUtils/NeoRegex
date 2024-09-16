package com.neo.regex.core.common.util

enum class UiMode {
    LIGHT,
    DARK;

    companion object
}

val UiMode.isDark: Boolean
    get() = this == UiMode.DARK
