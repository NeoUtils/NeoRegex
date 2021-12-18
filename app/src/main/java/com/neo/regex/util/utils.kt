package com.neo.regex.util

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.abs

@ColorInt
fun genColor(hsv: Int): Int {
    return Color.HSVToColor(floatArrayOf(hsv.toFloat(), 100f, 100f))
}

fun genHSV(position: Int, invert : Boolean = false): Int {
    val multiple = position / 251
    val hsv = position - multiple * 251
    return if (invert) abs(hsv - 250) else hsv
}
