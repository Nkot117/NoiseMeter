package com.nkot117.noisemeter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
        Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun IconTextOutlinedButton(
    buttonText: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconSize: Dp = Dimens.IconSmall,
    spacing: Dp = Dimens.IconSpacingSmall,
    contentDescription: String? = buttonText
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = Color.Unspecified
        )
        Spacer(Modifier.width(spacing))
        Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun IconTextButtonsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconTextButton(
                buttonText = "Filled Button",
                icon = Icons.Default.Favorite,
                onClick = {}
            )
            IconTextOutlinedButton(
                buttonText = "Outlined Button",
                icon = Icons.Default.FavoriteBorder,
                onClick = {}
            )
        }
    }
}
