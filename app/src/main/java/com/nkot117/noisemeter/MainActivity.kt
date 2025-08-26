package com.nkot117.noisemeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nkot117.noisemeter.ui.screen.mater.MeterScreen
import com.nkot117.noisemeter.ui.theme.NoiseMeterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoiseMeterTheme {
                MeterScreen()
            }
        }
    }
}

