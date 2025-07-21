package com.nkot117.noisemeter.ui.noise

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun NoiseMeterScreen(
    modifier: Modifier = Modifier
) {
    val dbLevel by remember { mutableFloatStateOf(0f) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("現在の騒音レベル", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(String.format("%.1f dB", dbLevel), style = MaterialTheme.typography.displayLarge)
    }
}

@Preview(apiLevel = 34)
@Preview(showBackground = true)
@Composable
fun PreviewNoiseMeterScreen() {
    NoiseMeterScreen()
}
