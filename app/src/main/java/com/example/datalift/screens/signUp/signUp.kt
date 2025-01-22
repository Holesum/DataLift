package com.example.datalift.screens.signUp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datalift.screens.logIn.accountCreationSwitch
import com.example.datalift.ui.components.DataliftDialogTextField
import com.example.datalift.ui.components.DataliftFormPrivateTextField
import com.example.datalift.ui.components.DataliftFormTextField
import com.example.datalift.ui.components.DataliftNumberTextField
import com.example.datalift.ui.components.DataliftTextField
import com.example.datalift.ui.components.DatePickerFieldToModal
import com.example.datalift.ui.components.RadioOptionFieldToModal
import com.example.datalift.ui.components.SemiStatelessRadioOptionFieldToModal
import com.example.datalift.ui.components.StatelessDataliftCDCardDialog
import com.example.datalift.ui.components.StatelessDataliftCardDialog
import com.example.datalift.ui.components.StatelessDataliftDialogTextField
import com.example.datalift.ui.components.StatelessDataliftFormPrivateTextField
import com.example.datalift.ui.components.StatelessDataliftFormTextField
import com.example.datalift.ui.components.StatelessDatePickerFieldToModal
import com.example.datalift.ui.theme.DataliftTheme
import kotlin.math.sign

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
    signUpViewModel: SignUpViewModel = viewModel(),
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
            modifier = Modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        
        Button(onClick = {navNext()}) {
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
            options = listOf("Male","Female","Prefer not to say"),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        StatelessDataliftFormTextField(
            field = "Height (inches)",
            suffix = "in.",
            text = signUpViewModel.height,
            changeText = signUpViewModel.updateHeight,
            modifier = Modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        StatelessDataliftFormTextField(
            field = "Weight (lb)",
            suffix = "lbs",
            text = signUpViewModel.weight,
            changeText = signUpViewModel.updateWeight,
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )

        StatelessDatePickerFieldToModal(
            date = signUpViewModel.dob,
            changeDate = signUpViewModel.updateDOB,
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)

        )

        Button(onClick = {navNext()}) {
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
            changeText = signUpViewModel.updatePassword,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = modifier.padding(4.dp)
                .fillMaxWidth(0.75f)
        )
        Button(onClick = {navNext()}) {
            Text(text = "Create Account")
        }
    }
}

@Preview(
    showBackground = true,
    group = "Baseline"
)
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

@Preview(
    showBackground = true,
    name = "Name Screen",
    group = "Name Screen"
)
@Composable
fun NameScreenPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            NameScreen(
                navUp = {},
                navNext = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Personal Information Screen",
    group = "PI Screen"
)
@Composable
fun PersonalInformationPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            PersonalInformationScreen(
                navUp = {},
                navNext = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "Credential Screen",
    group = "Credential Screen"
)
@Composable
fun CredentialsPreview(){
    DataliftTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ){ innerPadding ->
            CredentialsScreen(
                navUp = {},
                navNext = {},
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}