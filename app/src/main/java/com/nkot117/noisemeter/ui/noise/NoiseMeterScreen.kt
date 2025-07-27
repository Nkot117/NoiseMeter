package com.nkot117.noisemeter.ui.noise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            startRecording = { viewModel.startRecording() },
            stopRecording = { viewModel.stopRecording() },
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@Composable
fun NoiseMeterContent(
    uiState: NoiseUiState,
    startRecording: () -> Unit,
    stopRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        // 録音開始前のUIコンテンツ
        NoiseUiState.Initial -> ReadyToRecordContent(
            modifier = modifier, onStartClick = { startRecording() }
        )

        // 録音中のUIコンテンツ
        NoiseUiState.Recording -> RecordingContent(
            modifier = modifier, onStopClick = { stopRecording() }
        )

        is NoiseUiState.Error -> TODO()
    }

}

@Composable
fun ReadyToRecordContent(
    modifier: Modifier = Modifier,
    onStartClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "録音を開始する",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onStartClick()
            }
        ) {
            Text("開始")
        }
    }
}

@Composable
fun RecordingContent(
    modifier: Modifier = Modifier,
    onStopClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "録音中...",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onStopClick()
            }
        ) {
            Text("停止")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecordingContent() {
    RecordingContent(
        onStopClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewReadyToRecordContent() {
    ReadyToRecordContent(
        onStartClick = {},
    )
}