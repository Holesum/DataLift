package com.example.datalift

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.logIn.SignupScreen
//import com.example.datalift.screens.logIn.loginScreen
import com.example.datalift.ui.theme.DataliftTheme
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataliftApp()
        }
    }
}

@Composable
fun DataliftApp(){
    DataliftTheme {
        val navController = rememberNavController()
//        NavHost(
//            navController = navController,
//            startDestination = Login
//        ) {
//            composable<Login> { LoginScreen() }
//        }
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DataliftDestinations.LOGIN,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(DataliftDestinations.LOGIN) {
                    LoginScreen(
                        navigateToAccountCreation = {
                            navController.navigate(route = DataliftDestinations.SIGNUP)
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                composable(DataliftDestinations.SIGNUP) { SignupScreen() }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name! I'm making changes!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DataliftApp()
}