package com.example.cardguesser

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cardguesser.platform.reapplyAppLanguage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // The app is always dark, so keep light system-bar icons.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        // Dark window behind the UI so rotating into the round never flashes white.
        window.setBackgroundDrawable(ColorDrawable(0xFF131B3A.toInt()))

        setContent {
            App()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        // Android has just reset the language to the phone's; put the game's back before the UI updates.
        reapplyAppLanguage()
        super.onConfigurationChanged(newConfig)
    }
}
