package com.neo.regex.designsystem.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

val LocalFontSizes = compositionLocalOf<FontSizes> { error("FontSizes not defined") }

data class FontSizes(
    val huge: TextUnit = 20.sp,
    val big: TextUnit = 18.sp,
    val medium: TextUnit = 16.sp,
    val default: TextUnit = 14.sp,
    val small: TextUnit = 12.sp,
)