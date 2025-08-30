package com.nkot117.noisemeter.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nkot117.noisemeter.R
import com.nkot117.noisemeter.ui.navigation.Route
import com.nkot117.noisemeter.ui.theme.NoiseMeterTheme

@Composable
fun BottomBar(
    navController: NavController,
    selectedDestination: String,
    onDestinationSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        windowInsets = NavigationBarDefaults.windowInsets
    ) {
        NavigationBarItem(
            selected = selectedDestination == Route.Mater.toString(),
            onClick = {
                navController.navigate(Route.Mater)
                onDestinationSelected(Route.Mater.toString())
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_home),
                    modifier = Modifier.size(30.dp),
                    tint = Color.DarkGray,
                    contentDescription = "Mater",
                )
            },
            label = { Text("メーター", style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.weight(1f)
        )

        NavigationBarItem(
            selected = selectedDestination == Route.History.toString(),
            onClick = {
                navController.navigate(Route.History)
                onDestinationSelected(Route.History.toString())
            },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_history),
                    modifier = Modifier.size(30.dp),
                    tint = Color.DarkGray,
                    contentDescription = "History"
                )
            },
            label = { Text("履歴", style = MaterialTheme.typography.labelMedium) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    NoiseMeterTheme {
        val navController = rememberNavController()
        var selectedDestination by remember { mutableStateOf(Route.Mater.toString()) }

        BottomBar(
            navController = navController,
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}