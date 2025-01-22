package com.example.datalift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.signUp.CredentialsScreen
import com.example.datalift.screens.signUp.NameScreen
import com.example.datalift.screens.signUp.PersonalInformationScreen
import com.example.datalift.screens.workout.WorkoutListScreen
import com.example.datalift.ui.theme.DataliftTheme

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
                startDestination = LoginRoute,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<LoginRoute> {
                    LoginScreen(
                        navigateToAccountCreation = {
                            navController.navigate(route = SignUpBaseRoute)
                        },
                    )
                }

                navigation<SignUpBaseRoute>(startDestination = NameRoute)
                {
                    composable<NameRoute> {
                        NameScreen(
                            navUp = { navController.navigateUp() },
                            navNext = { navController.navigate(
                                route = PersonalInformationRoute
                            )}
                        )
                    }
                    composable<PersonalInformationRoute> {
                        PersonalInformationScreen(
                            navUp = { navController.navigateUp() },
                            navNext = { navController.navigate(
                                route = CredentialsRoute
                            )}
                        )
                    }
                    composable<CredentialsRoute> {
                        CredentialsScreen(
                            navUp = { navController.navigateUp() },
                            navNext = {
                                navController.navigate(route = WorkoutRoute){
                                    popUpTo(route = LoginRoute) { inclusive = true}
                                }
                            }
                        )
                    }
                }

                composable<WorkoutRoute> {
                    WorkoutListScreen(

                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DataliftApp()
}