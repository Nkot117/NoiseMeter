package com.nkot117.noisemeter.ui.screen.noise

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.nkot117.noisemeter.ui.common.IconTextOutlinedButton
import com.nkot117.noisemeter.ui.model.DbLevelCategory
import com.nkot117.noisemeter.ui.theme.LoudBg
import com.nkot117.noisemeter.ui.theme.LoudText
import com.nkot117.noisemeter.ui.theme.NormalBg
import com.nkot117.noisemeter.ui.theme.NormalText
import com.nkot117.noisemeter.ui.theme.QuietBg
import com.nkot117.noisemeter.ui.theme.QuietText
import com.nkot117.noisemeter.ui.theme.VeryLoudBg
import com.nkot117.noisemeter.ui.theme.VeryLoudText
import com.nkot117.noisemeter.ui.theme.VeryQuietBg
import com.nkot117.noisemeter.ui.theme.VeryQuietText
import timber.log.Timber

@SuppressLint("MissingPermission")
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
            .verticalScroll(scrollState)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 12.dp, end = 12.dp),
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
                var expanded by remember { mutableStateOf(false) }


                val (db, averageDb) = when (uiState) {
                    is NoiseUiState.Error -> Pair(0, 0)
                    NoiseUiState.Initial -> Pair(0, 0)
                    is NoiseUiState.Recording -> Pair(uiState.db, 0)
                    is NoiseUiState.Stopped -> Pair(
                        uiState.sessionUiData.lastDb,
                        uiState.sessionUiData.averageDb
                    )
                }

                NoiseMeterBody(
                    db = db,
                    averageDb = averageDb,
                    expanded
                )

                Spacer(Modifier.height(35.dp))

                when (uiState) {
                    NoiseUiState.Initial -> {
                        StartRecordingButton({
                            startRecording()
                            expanded = false
                        })
                    }

                    is NoiseUiState.Recording -> {
                        StopRecordingButton({
                            stopRecording()
                            expanded = true
                        })
                    }

                    is NoiseUiState.Stopped -> {
                        StartRecordingButton({
                            startRecording()
                            expanded = false
                        })
                    }

                    is NoiseUiState.Error -> {}
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        var expanded by remember { mutableStateOf(false) }
        val arrowRotation by animateFloatAsState(
            targetValue = if (expanded) 180f else 0f,
        )

        Card(
            modifier = Modifier.padding(bottom = 20.dp, start = 12.dp, end = 12.dp),
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
                        DbLevelCategory.entries.forEach { dbLeve ->
                            val (textColor, icon) = when (dbLeve) {
                                DbLevelCategory.QUIET -> Pair(VeryQuietText, R.drawable.ic_quit)
                                DbLevelCategory.SOFT -> Pair(QuietText, R.drawable.ic_soft)
                                DbLevelCategory.NORMAL -> Pair(NormalText, R.drawable.ic_normal)
                                DbLevelCategory.LOUD -> Pair(LoudText, R.drawable.ic_loud)
                                DbLevelCategory.VERY_LOUD -> Pair(
                                    VeryLoudText,
                                    R.drawable.ic_very_loud
                                )
                            }

                            DbLevelRow(
                                dbLevelCategory = dbLeve,
                                icon = ImageVector.vectorResource(icon),
                                textColor = textColor,
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
    averageDb: Int,
    expanded: Boolean
) {
    val progress = db / 120F
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500, easing = LinearOutSlowInEasing)
    )

    DbLevelDisplay(db)

    Spacer(Modifier.height(8.dp))

    DbLevelBar(animatedProgress)

    Spacer(Modifier.height(18.dp))

    DbLevelCard(db = db, averageDb = averageDb, expanded = expanded)
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
    db: Int,
    averageDb: Int,
    expanded: Boolean,
) {
    val (dbLevel, targetBgColor, targetTextColor) = when (db) {
        in DbLevelCategory.QUIET.min..DbLevelCategory.QUIET.max -> Triple(
            DbLevelCategory.QUIET,
            VeryQuietBg,
            VeryQuietText
        )

        in DbLevelCategory.SOFT.min..DbLevelCategory.SOFT.max -> Triple(
            DbLevelCategory.SOFT,
            QuietBg,
            QuietText
        )

        in DbLevelCategory.NORMAL.min..DbLevelCategory.NORMAL.max -> Triple(
            DbLevelCategory.NORMAL,
            NormalBg,
            NormalText
        )

        in DbLevelCategory.LOUD.min..DbLevelCategory.LOUD.max -> Triple(
            DbLevelCategory.LOUD,
            LoudBg,
            LoudText
        )

        else -> Triple(DbLevelCategory.VERY_LOUD, VeryLoudBg, VeryLoudText)
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
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dbLevel.label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor,
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(
                    animationSpec = tween(durationMillis = 600)
                ) + expandVertically(
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 600)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 600)
                ) + shrinkVertically(
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 600)
                )
            ) {
                // TODO: 測り終わった時のDBと平均のDBをどう出すか
                Column {
                    Text(averageDb.toString())
                    Text(dbLevel.example)
                }
            }
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
    IconTextOutlinedButton(
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

@Composable
fun DbLevelRow(
    dbLevelCategory: DbLevelCategory,
    icon: ImageVector,
    textColor: Color = Color.Black,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = dbLevelCategory.label,
            tint = Color.Unspecified,
            modifier = Modifier.size(56.dp)
        )

        Column(
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "${dbLevelCategory.min} ~ ${dbLevelCategory.max} dB",
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                text = dbLevelCategory.example,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Text(
            text = dbLevelCategory.label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(64.dp)
        )
    }
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
        uiState = NoiseUiState.Recording(db = 60),
        startRecording = {},
        stopRecording = {},
    )
}

@Preview(showBackground = true, name = "Stopped State")
@Composable
fun PreviewNoiseMeterContent_Stopped() {
    NoiseMeterContent(
        uiState = NoiseUiState.Stopped(
            NoiseSessionUiData(
                10, 15, 20, 30
            )
        ),
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
            DbLevelCard(db = 10, averageDb = 10, expanded = true)  // 非常に静か
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 30, averageDb = 30, expanded = true)  // 静か
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 50, averageDb = 50, expanded = true)  // 普通
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 70, averageDb = 70, expanded = true)  // 騒がしい
            Spacer(modifier = Modifier.height(20.dp))
            DbLevelCard(db = 90, averageDb = 90, expanded = true)  // 非常に騒がしい
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDbLevelRow() {
    DbLevelRow(
        dbLevelCategory = DbLevelCategory.VERY_LOUD,
        icon = ImageVector.vectorResource(R.drawable.ic_very_loud),
        textColor = VeryLoudText,
    )
}
