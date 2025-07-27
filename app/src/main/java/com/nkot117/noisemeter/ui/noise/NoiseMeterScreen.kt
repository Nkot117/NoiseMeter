package com.nkot117.noisemeter.ui.noise

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NoiseMeterScreen(
    viewModel: NoiseMeterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NoiseMeterContent(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun NoiseMeterContent(
    uiState: NoiseUiState,
    modifier: Modifier = Modifier
) {
    Text(text = "NoiseMeterContent")
}
