package com.example.datalift.screens.logIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.ui.components.StatelessDataliftFormPrivateTextField
import com.example.datalift.ui.components.StatelessDataliftFormTextField

@Composable
fun LoginFeatures(
    loginUiState: LoginUiState,
    changeUsername: (String) -> Unit,
    changePassword: (String) -> Unit,
    navigateToAccountCreation: () -> Unit,
    navigateToHome: () -> Unit,
    loginUser: (String, String) -> Unit,
    signinUser: () -> Unit,
    errorMessage: String?,
    actionMessage: String?,
    loggedin: Boolean,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    snackbarDisplayed: Boolean,
    closeSnackbar: () -> Unit,
    reSendVerificationEmail: () -> Unit,
    modifier: Modifier,
){
    LaunchedEffect(snackbarDisplayed) {
        if(snackbarDisplayed){
            val snackbarMessage = errorMessage ?: ""
            val snackbarResult = onShowSnackbar(snackbarMessage, actionMessage)
            if(snackbarResult){
                reSendVerificationEmail()
                closeSnackbar()
            } else {
                closeSnackbar()
            }
        }
    }

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
            text = loginUiState.username,
            changeText = changeUsername,
            isError = loginUiState.hasErrors,
            modifier = modifier.padding(4.dp)
        )
        StatelessDataliftFormPrivateTextField(
            field = "Password",
            text = loginUiState.password,
            changeText = changePassword,
            isError = loginUiState.hasErrors,
            supportingText = {
                if(loginUiState.hasErrors) {
                    Text(loginUiState.errorMessage)
                }
            },
            modifier = modifier.padding(4.dp)
        )
        Button(
            onClick = {
                loginUser(loginUiState.username, loginUiState.password)
                if(loggedin){
                    signinUser()
                    navigateToHome()
                }
            },
            enabled = loginUiState.canLogin
        ){
            Text("Login")
        }
        Spacer(Modifier.padding(8.dp))
        Button(onClick = { navigateToAccountCreation()}){
            Text("Account Creation")
        }
        Spacer(Modifier.padding(8.dp))
        if(loggedin){
            signinUser()
            navigateToHome()
        }
    }
}

@Composable
fun LoginScreen(
    logInViewModel: LogInViewModel = viewModel(),
    navigateToAccountCreation: () -> Unit,
    signinUser: () -> Unit,
    navigateToHome: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier
){
    val loginUiState by logInViewModel.uiState.collectAsStateWithLifecycle()
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
            loginUiState = loginUiState,
            changeUsername = logInViewModel.updateUsername,
            changePassword = logInViewModel.updatePassword,
            navigateToAccountCreation = navigateToAccountCreation,
            navigateToHome = navigateToHome,
            loginUser = logInViewModel::loginUser,  // Pass the login method
            signinUser = signinUser,
            errorMessage = logInViewModel.errorMessage.collectAsState().value, // Pass error message
            actionMessage = logInViewModel.actionMessage.collectAsState().value,
            loggedin = logInViewModel.loggedIn.collectAsState().value,
            onShowSnackbar = onShowSnackbar,
            snackbarDisplayed = logInViewModel.snackbarDisplayed,
            closeSnackbar = logInViewModel::closeSnackbar,
            reSendVerificationEmail = { logInViewModel.resendVerificationEmail() },
            modifier = modifier
        )
    }
}