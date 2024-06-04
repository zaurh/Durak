package com.zaurh.durak.presentation.screen.auth.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun AuthPassTrailingIcon(
    error: Boolean,
    onClick: () -> Unit,
    visibility: Boolean
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (error)
            Icon(
                Icons.Filled.Error,
                "error",
                tint = MaterialTheme.colorScheme.error
            )
        IconButton(onClick = {
            onClick()
        }) {
            Icon(
                imageVector =
                if (visibility)
                    Icons.Default.Visibility
                else
                    Icons.Default.VisibilityOff,
                contentDescription = "",
                tint = Color.Gray
            )
        }
    }
}