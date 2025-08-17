package com.nkot117.noisemeter.ui.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Dimens {
    val IconSmall = 20.dp
    val IconSpacingSmall = 8.dp
}

@Composable
fun IconTextButton(
    buttonText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconSize: Dp = Dimens.IconSmall,
    spacing: Dp = Dimens.IconSpacingSmall,
    contentDescription: String? = buttonText
) {
    Button(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = Color.Unspecified,
        )
        Spacer(Modifier.width(spacing))
        Text(text = buttonText)
    }
}

@Preview(showBackground = true)
@Composable
fun IconTextButtonPreview() {
    IconTextButton(
        buttonText = "設定",
        icon = Icons.Default.Settings,
        onClick = {}
    )
}
