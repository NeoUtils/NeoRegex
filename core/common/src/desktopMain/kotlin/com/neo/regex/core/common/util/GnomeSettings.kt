package com.neo.regex.core.common.util

object GnomeSettings {

    fun getUiMode(): UiMode {

        val process = ProcessBuilder(
            "gsettings",
            "get",
            "org.gnome.desktop.interface",
            "color-scheme",
        ).start().apply {
            waitFor()
        }

        val theme = process.inputStream.bufferedReader().readText()

        return when {
            theme.contains("dark") -> UiMode.DARK
            else -> UiMode.LIGHT
        }
    }
}