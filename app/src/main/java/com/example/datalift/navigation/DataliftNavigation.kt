package com.example.datalift.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.signUp.CredentialsScreen
import com.example.datalift.screens.signUp.NameScreen
import com.example.datalift.screens.signUp.PersonalInformationScreen
import com.example.datalift.screens.signUp.SignUpViewModel
import com.example.datalift.screens.workout.WorkoutDetailsScreen
import com.example.datalift.screens.workout.WorkoutListScreen
import com.example.datalift.screens.workout.WorkoutViewModel
import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object SignUpBaseRoute
@Serializable object NameRoute
@Serializable object PersonalInformationRoute
@Serializable object CredentialsRoute
@Serializable object WorkoutRoute
@Serializable object WorkoutListRoute


@Composable
fun AppNavigation() {
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
            startDestination = Screens.LogIn.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = Screens.LogIn.name){
                LoginScreen(
                    navigateToAccountCreation = {
                        navController.navigate(route = SignUpBaseRoute)
                    },
                    navigateToWorkoutList = {
                        navController.navigate(route = WorkoutRoute)
                    }
                )
            }

            signUpGraph(
                navController = navController
            )

            workoutGraph(
                navController = navController
            )

            /*composable(route = Screens.Workout.name) {
                val workoutViewModel: WorkoutViewModel = viewModel()
                WorkoutListScreen(
                    navController = navController,
                    workoutViewModel = workoutViewModel
                )
            }

            composable(route = Screens.WorkoutDetails.name) {
                WorkoutDetailsScreen(
                    workoutViewModel = viewModel()
                )

            }*/


        }
    }
}


fun NavGraphBuilder.signUpGraph(
    navController: NavController
) {
//    val signUpViewModel: SignUpViewModel by navGraphViewModels(route = SignUpBaseRoute)
    navigation<SignUpBaseRoute>(
        startDestination = NameRoute,
    )
    {
        composable<NameRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = SignUpBaseRoute)
            }

            val signUpViewModel: SignUpViewModel = viewModel(parentEntry)

            NameScreen(
                signUpViewModel = signUpViewModel,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(
                    route = PersonalInformationRoute
                )}
            )
        }
        composable<PersonalInformationRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = SignUpBaseRoute)
            }

            val signUpViewModel: SignUpViewModel = viewModel(parentEntry)

            PersonalInformationScreen(
                signUpViewModel = signUpViewModel,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(
                    route = CredentialsRoute
                )}
            )
        }
        composable<CredentialsRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = SignUpBaseRoute)
            }

            val signUpViewModel: SignUpViewModel = viewModel(parentEntry)
            CredentialsScreen(
                signUpViewModel = signUpViewModel,
                navUp = { navController.navigateUp() },
                navNext = {
                    navController.navigate(Screens.Workout.name){
                        popUpTo(route = LoginRoute) { inclusive = true}
                    }
                }
            )
        }
    }
}

fun NavGraphBuilder.workoutGraph(
    navController: NavController
) {
    navigation<WorkoutRoute>(
        startDestination = WorkoutListRoute
    )
    {
        composable<WorkoutListRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutRoute)
            }


            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)


            WorkoutListScreen(
                workoutViewModel = workoutViewModel,
                navController = navController,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(Screens.WorkoutDetails.name) }
            )
        }


        composable(route = Screens.WorkoutDetails.name) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutRoute)
            }


            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)
            WorkoutDetailsScreen(
                workoutViewModel = workoutViewModel,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(Screens.Workout.name) },
            )
            }
        }
    }



//object DataliftDestinations {
//    const val LOGIN = "signin"
//    const val SIGNUP = "signin/signup"
//    const val SIGNUP_NAME = "signin/signup/name"
//    const val SIGNUP_PI = "signin/signup/more_information"
//    const val SIGNUP_CREDENTIALS = "signin/signup/credentials"
//    const val HOME = "home"
//    const val WORKOUTS = "workout"
//
//}
