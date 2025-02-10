package com.example.datalift.screens.logIn

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.ui.components.StatelessDataliftFormPrivateTextField
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.theme.DataliftTheme

@Composable
fun LoginFeatures(
    username: String,
    password: String,
    changeUsername: (String) -> Unit,
    changePassword: (String) -> Unit,
    navigateToAccountCreation: () -> Unit,
    navigateToWorkoutList: () -> Unit,
    loginUser: (String, String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    loggedin: Boolean,
    undoLogin: () -> Unit,
    reSendVerificationEmail: () -> Unit,
    verPopup: Boolean,
    modifier: Modifier,
){
    var sendVer by remember { mutableStateOf(false) }

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
        Button(onClick = { loginUser(username, password); if(loggedin){navigateToWorkoutList()} else {sendVer = true} }, enabled = !isLoading){
            Text(if (isLoading) "Loading..." else "Login")
        }
        Spacer(Modifier.padding(8.dp))
        Button(onClick = { navigateToAccountCreation()}){
            Text("Account Creation")
        }
        Spacer(Modifier.padding(8.dp))
        if(sendVer && verPopup) {
            Button(onClick = {reSendVerificationEmail()}){
                Text("Resend Verification Email")
            }
        }
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }
        if(loggedin){
            undoLogin()
            navigateToWorkoutList()
        }
    }
}

@Composable
fun LoginScreen(
    logInViewModel: LogInViewModel = viewModel(),
    navigateToAccountCreation: () -> Unit,
    navigateToWorkoutList: () -> Unit,
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
        logInViewModel.loading.value?.let {
            LoginFeatures(
                username = logInViewModel.username,
                password = logInViewModel.password,
                changeUsername = logInViewModel.updateUsername,
                changePassword = logInViewModel.updatePassword,
                navigateToAccountCreation = navigateToAccountCreation,
                navigateToWorkoutList = navigateToWorkoutList,
                loginUser = logInViewModel::loginUser,  // Pass the login method
                isLoading = it, // Pass the loading state
                errorMessage = logInViewModel.errorMessage.collectAsState().value, // Pass error message
                loggedin = logInViewModel.loggedIn.collectAsState().value,
                undoLogin = logInViewModel::userLogged,
                reSendVerificationEmail = { logInViewModel.resendVerificationEmail() },
                verPopup = logInViewModel.verPopup.value!!,
                modifier = modifier
            )
        }
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
                navigateToWorkoutList = {},
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}