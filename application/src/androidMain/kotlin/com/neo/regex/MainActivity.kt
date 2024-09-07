package com.neo.regex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.neo.regex.designsystem.theme.NeoTheme
import com.neo.regex.ui.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
           NeoTheme {
               App()
           }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
   NeoTheme {
       App()
   }
}