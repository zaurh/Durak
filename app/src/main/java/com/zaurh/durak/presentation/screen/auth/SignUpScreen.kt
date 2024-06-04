package com.zaurh.durak.presentation.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.zaurh.durak.R
import com.zaurh.durak.common.MyCheckSignedIn
import com.zaurh.durak.common.MyProgressBar
import com.zaurh.durak.presentation.screen.auth.components.AuthErrorMessage
import com.zaurh.durak.presentation.screen.auth.components.AuthPassTrailingIcon
import com.zaurh.durak.presentation.screen.auth.components.AuthTextField
import com.zaurh.durak.presentation.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    MyCheckSignedIn(navController = navController, authViewModel = authViewModel)

    val context = LocalContext.current
    val isAuthLoading = authViewModel.isAuthLoading.value
    val focus = LocalFocusManager.current

    var dropdownIsExpanded by remember { mutableStateOf(false) }

    var usernameTf by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }

    var passwordTf by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }


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
                text = stringResource(id = R.string.signUp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 36.sp,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.size(120.dp))
            AuthTextField(
                value = usernameTf,
                onValueChange = { usernameTf = it },
                onDone = {
                    focus.clearFocus()
                },
                errorTf = usernameError,
                placeHolder = stringResource(id = R.string.email)
            )
            if (usernameError) {
                AuthErrorMessage(text = stringResource(id = R.string.enterEmail))
            }
            Spacer(modifier = Modifier.size(8.dp))
            AuthTextField(
                value = passwordTf,
                errorTf = passwordError,
                onValueChange = { passwordTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = stringResource(id = R.string.password),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                passwordVisibility = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    AuthPassTrailingIcon(
                        error = passwordError,
                        onClick = {
                            passwordVisibility = !passwordVisibility
                        },
                        visibility = passwordVisibility
                    )
                }
            )
            if (passwordError) {
                AuthErrorMessage(text = stringResource(id = R.string.enterPassword))
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                onClick = {
                    usernameError = usernameTf.isEmpty()
                    passwordError = passwordTf.isEmpty()

                    if (!usernameError && !passwordError) {
                        authViewModel.signUp(usernameTf, passwordTf, context)
                    }
                    focus.clearFocus()
                }) {
                Text(
                    text = stringResource(id = R.string.signUp),
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            Spacer(Modifier.size(32.dp))
            Divider()
            Spacer(Modifier.size(32.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(id = R.string.alreadyHaveAnAccount), fontSize = 14.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    },
                    text = stringResource(id = R.string.signIn),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.Underline
                )
            }

        }

        if (isAuthLoading) {
            MyProgressBar()
        }

    }

}