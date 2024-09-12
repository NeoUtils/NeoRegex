package com.neo.regex.core.common.platform

enum class LinuxUI {
    GNOME,
    OTHER;

    companion object {
        val Current: LinuxUI by lazy {
            val name = System.getenv("XDG_CURRENT_DESKTOP")
            when {
                name?.contains("GNOME") == true -> GNOME
                else -> OTHER
            }
        }
    }
}