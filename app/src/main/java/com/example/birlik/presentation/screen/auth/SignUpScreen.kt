package com.example.birlik.presentation.screen.auth

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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material3.Divider
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.birlik.R
import com.example.birlik.common.MyCheckSignedIn
import com.example.birlik.common.MyProgressBar
import com.example.birlik.presentation.screen.auth.components.AuthErrorMessage
import com.example.birlik.presentation.screen.auth.components.AuthPassTrailingIcon
import com.example.birlik.presentation.screen.auth.components.AuthTextField
import com.example.birlik.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterialApi::class)
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

    var usernameTf by remember { mutableStateOf("zaur@gmail.com") }
    var usernameError by remember { mutableStateOf(false) }

    var countryTf by remember { mutableStateOf("Azərbaycan") }
    var countryError by remember { mutableStateOf(false) }

    var passwordTf by remember { mutableStateOf("zaur1234") }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisibility by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color.White
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
            AuthTextField(
                value = usernameTf,
                onValueChange = { usernameTf = it },
                onDone = {
                    focus.clearFocus()
                },
                errorTf = usernameError,
                placeHolder = "İstifadəçi adı"
            )
            if (usernameError) {
                AuthErrorMessage(text = "İstifadəçi adı qeyd edin.")
            }
            Spacer(modifier = Modifier.size(8.dp))
            ExposedDropdownMenuBox(
                expanded = dropdownIsExpanded,
                onExpandedChange = { dropdownIsExpanded = !dropdownIsExpanded })
            {
                AuthTextField(
                    value = countryTf,
                    onValueChange = { countryTf = it },
                    onDone = { focus.clearFocus() },
                    placeHolder = "Yaşadığın ölkə ",
                    errorTf = countryError,
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownIsExpanded)
                    }
                )
                ExposedDropdownMenu(
                    expanded = dropdownIsExpanded,
                    onDismissRequest = { dropdownIsExpanded = false }) {
                    DropdownMenuItem(
                        onClick = {
                            countryTf = "Azərbaycan"
                            dropdownIsExpanded = false
                        }) {
                        Text(text = "Azərbaycan")
                    }
                    DropdownMenuItem(
                        onClick = {
                            countryTf = "Polşa"
                            dropdownIsExpanded = false
                        }) {
                        Text(text = "Polşa")
                    }
                    DropdownMenuItem(
                        onClick = {
                            countryTf = "Türkiyə"
                            dropdownIsExpanded = false
                        }) {
                        Text(text = "Türkiyə")
                    }
                }
            }
            if (countryError) {
                AuthErrorMessage(text = "Yaşadığınız ölkəni seçin.")
            }
            Spacer(modifier = Modifier.size(8.dp))
            AuthTextField(
                value = passwordTf,
                errorTf = passwordError,
                onValueChange = { passwordTf = it },
                onDone = {
                    focus.clearFocus()
                },
                placeHolder = "Şifrə",
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
                AuthErrorMessage(text = "Şifrənizi girin.")
            }
            Spacer(modifier = Modifier.size(30.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue)
                ),
                onClick = {
                    usernameError = usernameTf.isEmpty()
                    passwordError = passwordTf.isEmpty()
                    countryError = countryTf.isEmpty()

                    if (!usernameError && !passwordError && !countryError) {
                        authViewModel.signUp(usernameTf, countryTf, passwordTf, context)
                    }
                    focus.clearFocus()
                }) {
                Text(
                    text = "Qeydiyyat",
                    color = Color.White,
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                )
            }
            Spacer(Modifier.size(32.dp))
            Divider()
            Spacer(Modifier.size(32.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "Hesabın artıq var?", fontSize = 14.sp)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    },
                    text = "Giriş et",
                    fontSize = 14.sp,
                    color = colorResource(id = R.color.blue),
                    textDecoration = TextDecoration.Underline
                )
            }

        }

        if (isAuthLoading) {
            MyProgressBar()
        }

    }

}