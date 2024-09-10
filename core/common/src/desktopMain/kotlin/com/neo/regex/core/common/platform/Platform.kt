package com.neo.regex.core.common.platform

actual fun Platform.Companion.get(): Platform {
    return Platform.DESKTOP
}