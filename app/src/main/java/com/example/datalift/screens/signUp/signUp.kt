package com.example.datalift.screens.signUp

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
import com.example.datalift.screens.logIn.accountCreationSwitch
import com.example.datalift.ui.components.DataliftTextField
import com.example.datalift.ui.theme.DataliftTheme

@Composable
fun SignupFeatures(
    navUp: () -> Unit,
    modifier: Modifier
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Sign Up",
            modifier = modifier.padding(4.dp)
        )
        DataliftTextField(
            field = "Username",
            modifier = modifier.padding(4.dp)
        )
        DataliftTextField(
            field = "Password",
            modifier = modifier.padding(4.dp)
        )
        DataliftTextField(
            field = "Name",
            modifier = modifier.padding(4.dp)
        )
        DataliftTextField(
            field = "Weight (lb)",
            modifier = modifier.padding(4.dp)
        )
        DataliftTextField(
            field = "Height (inches)",
            modifier = modifier.padding(4.dp)
        )
        Button(onClick = { accountCreationSwitch() }){
            Text("Sign Up")
        }

        Spacer(Modifier.padding(8.dp))
        Button(onClick = { navUp() }){
            Text("Login")
        }
    }
}

@Composable
fun SignupScreen(
    navUp: () -> Unit,
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
        SignupFeatures(
            navUp = navUp,
            modifier = modifier
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SignUpPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            SignupScreen(
                navUp = {},
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}