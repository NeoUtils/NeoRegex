package com.neo.utilskt

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Context.dp(size: Int): Int = dp(size.toFloat()).toInt()
fun Fragment.dp(size: Int) = requireContext().dp(size)

fun Context.dp(size: Float): Float = size * resources.getDimension(R.dimen.dimen_1dp)
fun Fragment.dp(size: Float) = requireContext().dp(size)

@ColorInt
fun Resources.Theme.color(colorRes: Int): Int {
    val typedValue = TypedValue()
    this.resolveAttribute(colorRes, typedValue, true)
    return typedValue.data
}

@ColorInt
fun Context.color(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)
fun Fragment.color(@ColorRes colorRes: Int) = requireContext().color(colorRes)

fun runOnMainThread(delay: Long = 0, function: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed(function, delay)
}

fun Context.dialog(title : String, message : String) : AlertDialog {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
    builder.setMessage(message)
    return builder.show()
}