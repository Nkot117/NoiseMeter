package com.nkot117.noisemeter.ui.noise

import android.Manifest
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import timber.log.Timber

@androidx.annotation.RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
@Composable
fun NoiseMeterScreen(
    viewModel: NoiseMeterViewModel = hiltViewModel(),
) {
    Timber.d("NoiseMeterScreen Compose")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        NoiseMeterContent(
            uiState = uiState,
            recordingAction = {
                viewModel.startRecording()
                              },
            modifier = Modifier.padding((innerPadding)),
        )
    }
}


@Composable
fun NoiseMeterContent(
    uiState: NoiseUiState,
    recordingAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (
        uiState
    ) {
        NoiseUiState.Initial -> InitialContent(db = 0, modifier = modifier,recordingAction)
        is NoiseUiState.Recording -> InitialContent(db = uiState.dbLevel, modifier = modifier, {})
        is NoiseUiState.Error -> TODO()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InitialContent(
    db: Int,
    modifier: Modifier = Modifier,
    recordingAction: () -> Unit
) {
    val audioPermissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val progress = db / 120F
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "音量メーター", style = MaterialTheme.typography.titleLarge)

        // デジベル数値の表示
        Text(
            text = db.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "dB",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(Modifier.height(8.dp))

        // プログレスバー
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .height(30.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color.LightGray)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0dB", style = MaterialTheme.typography.labelLarge)
            Text("60dB", style = MaterialTheme.typography.labelLarge)
            Text("120dB", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier
                .height(48.dp)
                .width(500.dp),
            onClick = {
                if (audioPermissionState.status === PermissionStatus.Granted) {
                    recordingAction()
                } else {
                    audioPermissionState.launchPermissionRequest()
                }
            },
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "測定開始")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoiseMeterContent() {
    InitialContent(20, recordingAction = {})
}