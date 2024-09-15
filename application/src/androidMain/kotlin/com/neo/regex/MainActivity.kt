package com.neo.regex

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
        val style = when (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
            UI_MODE_NIGHT_YES -> {
                SystemBarStyle.dark(
                    Color.BLACK,
                )
            }

            else -> {
                SystemBarStyle.light(
                    Color.WHITE,
                    Color.BLACK,
                )
            }
        }

        enableEdgeToEdge(style, style)
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    NeoTheme {
        App()
    }
}