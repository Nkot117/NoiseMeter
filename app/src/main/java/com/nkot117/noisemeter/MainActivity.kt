package com.nkot117.noisemeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nkot117.noisemeter.ui.screen.noise.NoiseMeterScreen
import com.nkot117.noisemeter.ui.theme.NoiseMeterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoiseMeterTheme {
                NoiseMeterScreen()
            }
        }
    }
}

