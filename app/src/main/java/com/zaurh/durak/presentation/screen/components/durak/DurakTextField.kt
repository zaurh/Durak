@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.durak.presentation.screen.components.durak

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DurakTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    placeHolder: String,
    leadingIcon: Painter? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    changeMoney: () -> Unit = {}
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
        ),
        keyboardActions = KeyboardActions(onDone = {
            onDone()
        }),
        singleLine = true,
        placeholder = {
            Text(
                text = placeHolder,
                color = MaterialTheme.colorScheme.secondary
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Image(
                    modifier = Modifier
                        .size(34.dp)
                        .clickable {
                            changeMoney()
                        },
                    painter = it,
                    contentDescription = null,
                )
            }
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions
    )
}