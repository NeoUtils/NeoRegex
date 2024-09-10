package com.neo.regex.core.common.platform

enum class Platform {
    DESKTOP,
    ANDROID;

    companion object
}

expect fun Platform.Companion.get(): Platform