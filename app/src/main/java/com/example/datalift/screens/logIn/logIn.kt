package com.example.datalift.screens.logIn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.datalift.ui.theme.DataliftTheme

fun accountCreationSwitch(){
    return
}

@Composable
fun SignupFeatures(modifier: Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Sign Up",
            modifier = modifier.padding(4.dp)
        )
        LoginTextField(
            field = "Username",
            modifier = modifier.padding(4.dp)
        )
        LoginTextField(
            field = "Password",
            modifier = modifier.padding(4.dp)
        )
        Button(onClick = { accountCreationSwitch()}){
            Text("Sign Up")
        }
        Spacer(Modifier.padding(8.dp))
        Button(onClick = { accountCreationSwitch()}){
            Text("Login")
        }
    }
}

@Composable
fun LoginFeatures(
    navigateToAccountCreation: () -> Unit,
    modifier: Modifier,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Login",
            modifier = modifier.padding(4.dp)
        )
        LoginTextField(
            field = "Username",
            modifier = modifier.padding(4.dp)
        )
        LoginTextField(
            field = "Password",
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
fun LoginTextField(field: String, modifier: Modifier = Modifier){
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { updateText -> text = updateText},
        label = { Text(text = field) },
        modifier = modifier
    )
}

@Composable
fun LoginScreen(
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
            navigateToAccountCreation = navigateToAccountCreation,
            modifier = modifier
        )
    }
}

@Composable
fun SignupScreen(
    modifier: Modifier = Modifier
) {
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
        SignupFeatures(modifier)
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