package com.nkot117.noisemeter.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nkot117.noisemeter.ui.screen.history.HistoryScreen
import com.nkot117.noisemeter.ui.screen.mater.MeterScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Mater
    ) {
        composable<Route.Mater> {
            MeterScreen(modifier = modifier)
        }

        composable<Route.History> {
            HistoryScreen()
        }
    }
}