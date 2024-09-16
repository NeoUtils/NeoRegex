package com.neo.regex

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import com.neo.regex.core.common.util.UiMode
import com.neo.regex.core.common.util.resolve
import com.neo.regex.core.designsystem.theme.NeoTheme
import com.neo.regex.ui.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupSystemBars()

        setContent {
            NeoTheme {
                App(Modifier.safeDrawingPadding())
            }
        }
    }

    private fun setupSystemBars() {
        val style = when (UiMode.resolve(context = this)) {
            UiMode.DARK -> {
                SystemBarStyle.dark(
                    Color.BLACK,
                )
            }

            UiMode.LIGHT -> {
                SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK,
                )
            }
        }

        enableEdgeToEdge(style, style)
    }
}
