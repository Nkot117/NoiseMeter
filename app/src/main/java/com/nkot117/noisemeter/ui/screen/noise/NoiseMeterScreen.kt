package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.nkot117.noisemeter.R
import com.nkot117.noisemeter.ui.common.IconTextButton
import com.nkot117.noisemeter.ui.theme.NoisyBg
import com.nkot117.noisemeter.ui.theme.NoisyText
import com.nkot117.noisemeter.ui.theme.NormalBg
import com.nkot117.noisemeter.ui.theme.NormalText
import com.nkot117.noisemeter.ui.theme.QuietBg
import com.nkot117.noisemeter.ui.theme.QuietText
import com.nkot117.noisemeter.ui.theme.VeryNoisyBg
import com.nkot117.noisemeter.ui.theme.VeryNoisyText
import com.nkot117.noisemeter.ui.theme.VeryQuietBg
import com.nkot117.noisemeter.ui.theme.VeryQuietText
import timber.log.Timber

@RequiresPermission(Manifest.permission.RECORD_AUDIO)
@Composable
fun NoiseMeterScreen(
    viewModel: NoiseMeterViewModel = hiltViewModel(),
) {
    Timber.d("NoiseMeterScreen Compose")
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
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
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
            .verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = modifier
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "音量メーター", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(24.dp))

                Timber.d("Current Ui State: $uiState")
                when (uiState) {
                    NoiseUiState.Initial -> {
                        // 初期表示時
                        NoiseMeterBody(
                            db = 0
                        )
                        Spacer(Modifier.height(18.dp))
                        DbLevelCard(0)
                        Spacer(Modifier.height(35.dp))
                        StartRecordingButton(startRecording)
                    }

                    is NoiseUiState.Recording -> {
                        // レコーディング中
                        NoiseMeterBody(
                            db = uiState.dbLevel,
                        )
                        Spacer(Modifier.height(18.dp))
                        DbLevelCard(uiState.dbLevel)
                        Spacer(Modifier.height(35.dp))
                        StopRecordingButton(stopRecording)
                    }

                    is NoiseUiState.Stopped -> {
                        // 停止中
                        NoiseMeterBody(
                            db = uiState.dbLevel
                        )
                        Spacer(Modifier.height(18.dp))
                        DbLevelCard(uiState.dbLevel)
                        Spacer(Modifier.height(35.dp))
                        StartRecordingButton(startRecording)
                    }

                    is NoiseUiState.Error -> {}
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        var expanded by remember { mutableStateOf(true) }
        val arrowRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
        )

        Card(
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .clickable { expanded = !expanded },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "デシベル参考表",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.rotate(arrowRotation)
                    )
                }

                Spacer(Modifier.height(18.dp))

                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(
                        expandFrom = Alignment.Top,
                        animationSpec = tween()
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Top,
                        animationSpec = tween()
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_quit),
                                contentDescription = "非常に静か",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )

                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "0 ~ 20 dB",
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = "図書館、深夜の住宅地",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Text(
                                text = "非常に静か",
                                color = VeryQuietText,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_soft),
                                contentDescription = "静か",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )

                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "21 ~ 40 dB",
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = "静かなカフェ、読書室",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Text(
                                text = "静か",
                                color = QuietText,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_normal),
                                contentDescription = "普通",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )

                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "41 ~ 60 dB",
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = "日常会話、オフィス",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Text(
                                text = "普通",
                                color = NormalText,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        //

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_loud),
                                contentDescription = "騒がしい",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )

                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "61 ~ 80 dB",
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = "賑やかな街中、掃除機",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Text(
                                text = "騒がしい",
                                color = NoisyText,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(64.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))


                        //

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_very_loud),
                                contentDescription = "非常に騒がしい",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(56.dp)
                            )

                            Column(
                                modifier = Modifier.width(200.dp)
                            ) {
                                Text(
                                    text = "81+ dB",
                                    style = MaterialTheme.typography.labelLarge,
                                )

                                Text(
                                    text = "車の通る道路、電車",
                                    style = MaterialTheme.typography.labelMedium,
                                )
                            }
                            Text(
                                text = "非常に騒がしい",
                                color = VeryNoisyText,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.width(64.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoiseMeterBody(
    db: Int,
) {
    val progress = db / 120F
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    DbLevelDisplay(db)

    Spacer(Modifier.height(8.dp))

    DbLevelBar(animatedProgress)
}

@Composable
fun DbLevelDisplay(
    db: Int
) {
    Text(
        text = db.toString(),
        style = MaterialTheme.typography.displayLarge,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = "dB", style = MaterialTheme.typography.displaySmall, color = Color.Gray
    )
}

@Composable
fun DbLevelBar(
    progress: Float
) {
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
                .fillMaxWidth(progress)
                .background(MaterialTheme.colorScheme.primary)
        )
    }

    Spacer(Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("0dB", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text("60dB", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text("120dB", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
    }
}

@Composable
fun DbLevelCard(
    db: Int
) {
    val (dbLevel, targetBgColor, targetTextColor) = when (db) {
        in 0..20 -> Triple("非常に静か", VeryQuietBg, VeryQuietText)
        in 21..40 -> Triple("静か", QuietBg, QuietText)
        in 41..60 -> Triple("普通", NormalBg, NormalText)
        in 61..80 -> Triple("騒がしい", NoisyBg, NoisyText)
        else -> Triple("非常に騒がしい", VeryNoisyBg, VeryNoisyText)
    }

    val backgroundColor by animateColorAsState(
        targetValue = targetBgColor,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "bgColorAnim"
    )
    val textColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(durationMillis = 600, easing = LinearOutSlowInEasing),
        label = "textColorAnim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),

        ) {
        Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(
                text = dbLevel,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StartRecordingButton(
    clickAction: () -> Unit
) {
    val isPreview = LocalInspectionMode.current
    val audioPermissionState = if (!isPreview) {
        rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    } else {
        null
    }

    IconTextButton(
        buttonText = "測定開始",
        icon = ImageVector.vectorResource(id = R.drawable.ic_measure_start),
        modifier = Modifier
            .height(48.dp),
        iconSize = 48.dp,
        onClick = {
            if (audioPermissionState?.status == PermissionStatus.Granted) {
                clickAction()
            } else {
                audioPermissionState?.launchPermissionRequest()
            }
        },
        contentDescription = "測定停止ボタン"
    )
}

@Composable
fun StopRecordingButton(
    clickAction: () -> Unit
) {
    IconTextButton(
        buttonText = "測定停止",
        icon = ImageVector.vectorResource(id = R.drawable.ic_measure_stop),
        modifier = Modifier
            .height(48.dp),
        iconSize = 48.dp,
        onClick = {
            clickAction()
        },
        contentDescription = "測定停止ボタン"
    )
}

@Preview(showBackground = true, name = "Initial State")
@Composable
fun PreviewNoiseMeterContent_Initial() {
    NoiseMeterContent(
        uiState = NoiseUiState.Initial,
        startRecording = {},
        stopRecording = {},
    )
}

@Preview(showBackground = true, name = "Recording State")
@Composable
fun PreviewNoiseMeterContent_Recording() {
    NoiseMeterContent(
        uiState = NoiseUiState.Recording(dbLevel = 60),
        startRecording = {},
        stopRecording = {},
    )
}

@Preview(showBackground = true, name = "Stopped State")
@Composable
fun PreviewNoiseMeterContent_Stopped() {
    NoiseMeterContent(
        uiState = NoiseUiState.Stopped(dbLevel = 35),
        startRecording = {},
        stopRecording = {},
    )
}

@Preview(showBackground = true, name = "Error State")
@Composable
fun PreviewNoiseMeterContent_Error() {
    NoiseMeterContent(
        uiState = NoiseUiState.Error(message = "録音エラー"),
        startRecording = {},
        stopRecording = {},
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewDbLevelCard() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            DbLevelCard(db = 10)  // 非常に静か
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 30)  // 静か
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 50)  // 普通
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 70)  // 騒がしい
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 90)  // 非常に騒がしい
        }
    }
}
