package com.example.datalift.screens.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.R
import com.example.datalift.ui.components.DataliftFormPrivateTextField
import com.example.datalift.ui.components.DataliftFormTextField
import com.example.datalift.ui.components.DataliftNumberTextField
import com.example.datalift.ui.components.DatePickerFieldToModal
import com.example.datalift.ui.components.RadioOptionFieldToModal
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.example.datalift.ui.components.StatelessDataliftFormPrivateTextField
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.components.StatelessDataliftNumberTextField

@Composable
fun SignupFeatures(
    navUp: () -> Unit,
    modifier: Modifier
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Sign Up",
            modifier = modifier.padding(4.dp)
        )

        RadioOptionFieldToModal(
            field = "Gender",
            options = listOf("Male","Female","Prefer not to say"),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        DataliftFormTextField(
            field = "Username",
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DataliftFormPrivateTextField(
            field = "Password",
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DataliftFormTextField(
            field = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DataliftFormTextField(
            field = "Name",
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DataliftNumberTextField(
            field = "Weight (lb)",
            suffix = "lbs",
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DataliftNumberTextField(
            field = "Height (inches)",
            suffix = "in.",
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        DatePickerFieldToModal(
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        Button(onClick = { }){
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

@Composable
fun NameScreen(
    signUpViewModel: SignUpViewModel = viewModel(),
    navUp: () -> Unit,
    navNext: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        IconButton(onClick = navUp) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back"
            )
        }
        Text(
            text = "Tell Us About Yourself"
        )
        StatelessDataliftFormTextField(
            field = "Name",
            text = signUpViewModel.name,
            changeText = signUpViewModel.updateName,
            supportingText = {
                if(signUpViewModel.nameInvalid) {
                    Text("Name need to be atleast 1 character")
                }
            },
            trailingIcon = {
                if(signUpViewModel.nameInvalid){
                    Icon(
                        painter =  painterResource(R.drawable.error),
                        contentDescription = null,
                    )
                }
            },
            isError = signUpViewModel.nameInvalid,

            modifier = Modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        Button(onClick = {
            if(signUpViewModel.nameValidated()){
                navNext()
            }
        }) {
            Text(text = "Next")
        }
    }
}

@Composable
fun PersonalInformationScreen(
    signUpViewModel: SignUpViewModel = viewModel(),
    navUp: () -> Unit,
    navNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        IconButton(onClick = navUp) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back"
            )
        }
        Text(
            text = "Tell Us About Yourself"
        )
        SemiStatelessRadioOptionFieldToModal(
            field = "Gender",
            selectedOption = signUpViewModel.gender,
            changeSelectedOption = signUpViewModel.updateGender,
            isError = signUpViewModel.genderInvalid,
            supportingText = {
                if(signUpViewModel.genderInvalid) {
                    Text("Need to choose a selection")
                }
            },
            options = listOf("Male","Female","Prefer not to say"),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        StatelessDataliftNumberTextField(
            field = "Height (inches)",
            suffix = "in.",
            text = signUpViewModel.height,
            changeText = signUpViewModel.updateHeight,
            isError = signUpViewModel.heightInvalid,
            supportingText = {
                if(signUpViewModel.heightInvalid) {
                    Text("Height needs to be an un-empty field")
                }
            },
            modifier = Modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        StatelessDataliftNumberTextField(
            field = "Weight (lb)",
            suffix = "lbs",
            text = signUpViewModel.weight,
            changeText = signUpViewModel.updateWeight,
            isError = signUpViewModel.weightInvalid,
            supportingText = {
                if(signUpViewModel.weightInvalid) {
                    Text("Weight needs to be an un-empty field")
                }
            },
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        Button(onClick = {
                if(signUpViewModel.personalCredentialsValidated()){
                    navNext()
                }
            }
        ) {
            Text(text = "Next")
        }
    }
}

@Composable
fun CredentialsScreen(
    signUpViewModel: SignUpViewModel = viewModel(),
    navUp: () -> Unit,
    navNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        IconButton(onClick = navUp) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Navigate Back"
            )
        }
        Text(
            text = "Create your account"
        )
        StatelessDataliftFormTextField(
            field = "Username",
            text = signUpViewModel.username,
            changeText = signUpViewModel.updateUsername,
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        StatelessDataliftFormPrivateTextField(
            field = "Password",
            text = signUpViewModel.password,
            changeText = signUpViewModel.updatePassword,
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        StatelessDataliftFormTextField(
            field = "Email",
            text = signUpViewModel.email,
            changeText = signUpViewModel.updateEmail,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        Button(onClick = {
            if(signUpViewModel.accountInformationValidated()){
                signUpViewModel.createDBUser()
                val signedUp = signUpViewModel.accountCreated.value
                if(signedUp) {
                    signUpViewModel.naving()
                    navNext()
                }
            }

        }) { Text(text = "Create Account") }
    }

}