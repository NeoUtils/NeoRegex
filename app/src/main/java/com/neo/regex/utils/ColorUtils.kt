package com.neo.regex.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import kotlin.math.abs

@ColorInt
fun genColor(hsv: Int): Int {
    return Color.HSVToColor(floatArrayOf(hsv.toFloat(), 100f, 100f))
}

fun genHSV(position: Int,  max : Int = 250, invert : Boolean = false): Int {
    val multiple = position / (max + 1)
    val hsv = position - multiple * (max + 1)
    return if (invert) abs( max - hsv) else hsv
}

fun main() {
    for (index in 0..1000) {
        println(genHSV(index * 15, 240, true))
    }
}