package com.neo.regex.core.common.util

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES

fun UiMode.Companion.resolve(context: Context): UiMode {

    return when (context.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
        UI_MODE_NIGHT_YES -> {
            UiMode.DARK
        }

        else -> {
            UiMode.LIGHT
        }
    }
}
