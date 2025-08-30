package com.nkot117.noisemeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nkot117.noisemeter.ui.components.BottomBar
import com.nkot117.noisemeter.ui.navigation.AppNavHost
import com.nkot117.noisemeter.ui.navigation.Route
import com.nkot117.noisemeter.ui.theme.NoiseMeterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoiseMeterTheme {
                val navController = rememberNavController()
                var selectedDestination by rememberSaveable { mutableStateOf(Route.Mater.toString()) }

                Scaffold(
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            selectedDestination = selectedDestination,
                            onDestinationSelected = { selectedDestination = it }
                        )
                    }

                ) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

