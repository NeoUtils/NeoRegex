package com.neo.regex.ui.screen.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

class AboutScreen : Screen {

    @Composable
    override fun Content() = Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "About Screen",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}