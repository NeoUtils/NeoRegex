package com.neo.regex.core.common.platform

enum class Platform {
    DESKTOP,
    ANDROID;

    companion object
}

expect val Platform.Companion.Current: Platform