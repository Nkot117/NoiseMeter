package com.nkot117.noisemeter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
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
                        NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                            NavigationBarItem(
                                selected = selectedDestination == Route.Mater.toString(),
                                onClick = {
                                    navController.navigate(Route.Mater)
                                    selectedDestination = Route.Mater.toString()
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Mater",
                                    )
                                },
                                label = {
                                    Text(
                                        text = "メーター",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                            )

                            NavigationBarItem(
                                selected = selectedDestination == Route.History.toString(),
                                onClick = {
                                    navController.navigate(Route.History)
                                    selectedDestination = Route.History.toString()
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "History"
                                    )
                                },
                                label = {
                                    Text(
                                        text = "履歴",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
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

