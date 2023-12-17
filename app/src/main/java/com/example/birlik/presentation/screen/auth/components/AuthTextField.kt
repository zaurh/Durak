package com.example.birlik.presentation.screen.auth.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.birlik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    errorTf: Boolean = false,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    passwordVisibility: VisualTransformation? = null,
    placeHolder: String,
    trailingIcon: @Composable () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                color = if (errorTf) Color.Red else colorResource(id = R.color.grey),
                RoundedCornerShape(10.dp)
            ),
        readOnly = readOnly,
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            color = Color.Black,
            fontSize = 16.sp
        ),
        keyboardActions = KeyboardActions(onDone = {
            onDone()
        }),
        singleLine = true,
        visualTransformation = passwordVisibility ?: VisualTransformation.None,
        placeholder = {
            Text(
                text = placeHolder,
                color = Color.DarkGray,
                modifier = Modifier.alpha(0.5f)
            )
        },
        trailingIcon = {
            trailingIcon()
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = colorResource(id = R.color.light_grey),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = keyboardOptions
    )
}