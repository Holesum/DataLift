package com.example.datalift.screens.logIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
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

        LoginFields(
            usernameInput = loginUiState.username,
            passwordInput = loginUiState.password,
            changeUsername = changeUsername,
            changePassword = changePassword,
            errorMessage = loginUiState.errorMessage,
            hasErrors = loginUiState.hasErrors,
            loginUser = {
                loginUser(loginUiState.username, loginUiState.password)
                if (loggedin) {
                    signinUser()
                    navigateToHome()
                }
            }
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
fun LoginFields(
    usernameInput: String,
    passwordInput: String,
    changePassword: (String) -> Unit,
    changeUsername: (String) -> Unit,
    errorMessage: String,
    loginUser: () -> Unit,
    hasErrors: Boolean,
    modifier: Modifier = Modifier
){
    val (first, second) = remember { FocusRequester.createRefs() }

    StatelessDataliftFormTextField(
        field = "Email",
        text = usernameInput,
        changeText = changeUsername,
        isError = hasErrors,
        keyboardActions = KeyboardActions(
            onNext = { second.requestFocus() }
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
        ),
        modifier = modifier.padding(4.dp)
            .focusRequester(first)
            .focusProperties { next = second }
    )
    DataliftPasswordLoginField(
        text = passwordInput,
        changeText = changePassword,
        isError = hasErrors,
        errorMessage = errorMessage,
        loginUser = loginUser,
        modifier = modifier.padding(4.dp)
            .focusRequester(second)
    )
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

@Composable
fun DataliftPasswordLoginField(
    text: String,
    changeText: (String) -> Unit,
    isError: Boolean,
    errorMessage: String,
    loginUser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val closeKeyboard = {
        keyboardController?.hide()
    }

    StatelessDataliftFormPrivateTextField(
        field = "Password",
        text = text,
        changeText = changeText,
        isError = isError,
        supportingText = {
            if(isError) {
                Text(errorMessage)
            }
        },
        imeAction = ImeAction.Go,
        keyboardActions = KeyboardActions(
            onGo = {
                loginUser()
                closeKeyboard()
            }
        ),
        modifier = modifier
    )
}