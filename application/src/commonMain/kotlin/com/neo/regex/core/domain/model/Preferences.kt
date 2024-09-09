package com.neo.regex.core.domain.model

data class Preferences(
    val uiMode: UiMode = UiMode.DARK
) {
    enum class UiMode(val key: String) {
        LIGHT(key = "light"),
        DARK(key = "dark");

        fun toggle(): UiMode {
            return when (this) {
                LIGHT -> DARK
                DARK -> LIGHT
            }
        }

        companion object {
            fun get(key: String): UiMode {

                return entries.find {
                    it.key == key
                } ?: DARK
            }
        }
    }

    companion object {
        val Default = Preferences()
    }
}