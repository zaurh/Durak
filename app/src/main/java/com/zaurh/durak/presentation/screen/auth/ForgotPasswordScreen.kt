@file:OptIn(ExperimentalMaterial3Api::class)

package com.zaurh.durak.presentation.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zaurh.durak.R
import com.zaurh.durak.common.MyProgressBar
import com.zaurh.durak.presentation.screen.auth.components.AuthTextField
import com.zaurh.durak.presentation.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val isLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current
    var emailTf by remember { mutableStateOf("") }
    var emailTfError by remember { mutableStateOf(false) }

    val context = LocalContext.current


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface
            )

    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(30.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.forgotPassword),
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(40.dp))
            Text(
                text = stringResource(id = R.string.forgotPasswordText),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.size(20.dp))
            AuthTextField(
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                value = emailTf,
                onValueChange = { emailTf = it },
                onDone = { focus.clearFocus() },
                placeHolder = "E-mail"
            )
            Spacer(modifier = Modifier.size(8.dp))

            if (emailTfError) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Error,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = stringResource(id = R.string.enterEmail),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.size(30.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                onClick = {
                    if (emailTf.isEmpty()) {
                        emailTfError = true
                    } else {
                        authViewModel.forgotPassword(emailTf, context)
                        focus.clearFocus()
                    }
                }) {
                Text(
                    text = stringResource(id = R.string.send),
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }

        }

        if (isLoading) {
            MyProgressBar()
        }
    }

}