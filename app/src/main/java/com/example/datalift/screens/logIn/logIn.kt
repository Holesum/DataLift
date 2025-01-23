package com.example.datalift.screens.logIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.ui.components.DataliftFormPrivateTextField
import com.example.datalift.ui.components.DataliftFormTextField
import com.example.datalift.ui.components.StatelessDataliftFormPrivateTextField
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.theme.DataliftTheme
import kotlin.math.log

fun accountCreationSwitch(){
    return
}

@Composable
fun LoginFeatures(
    username: String,
    password: String,
    changeUsername: (String) -> Unit,
    changePassword: (String) -> Unit,
    navigateToAccountCreation: () -> Unit,
    modifier: Modifier,
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Login",
            modifier = modifier.padding(4.dp)
        )
        StatelessDataliftFormTextField(
            field = "Username",
            text = username,
            changeText = changeUsername,
            modifier = modifier.padding(4.dp)
        )
        StatelessDataliftFormPrivateTextField(
            field = "Password",
            text = password,
            changeText = changePassword,
            modifier = modifier.padding(4.dp)
        )
        Button(onClick = { accountCreationSwitch()}){
            Text("Login")
        }
        Spacer(Modifier.padding(8.dp))
        Button(onClick = { navigateToAccountCreation()}){
            Text("Account Creation")
        }
    }
}

@Composable
fun LoginScreen(
    logInViewModel: LogInViewModel = viewModel(),
    navigateToAccountCreation: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "DATALIFT",
            fontFamily = FontFamily.Serif,
            fontSize = 48.sp,
            modifier = modifier.padding(16.dp)
        )
        LoginFeatures(
            username = logInViewModel.username,
            password = logInViewModel.password,
            changeUsername = logInViewModel.updateUsername,
            changePassword = logInViewModel.updatePassword,
            navigateToAccountCreation = navigateToAccountCreation,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            LoginScreen(
                navigateToAccountCreation = {},
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}