package com.neo.regex.core

enum class Platform() {
    DESKTOP,
    ANDROID;
}

expect fun getPlatform(): Platform