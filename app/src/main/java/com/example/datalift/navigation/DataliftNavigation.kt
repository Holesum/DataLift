package com.example.datalift.navigation

import android.util.Log
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.datalift.screens.analysis.AnalysisScreen
import com.example.datalift.screens.feed.FeedScreen
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
@Serializable object FeedBaseRoute
@Serializable object FeedRoute
@Serializable object SignUpBaseRoute
@Serializable object NameRoute
@Serializable object PersonalInformationRoute
@Serializable object CredentialsRoute
@Serializable object WorkoutBaseRoute
@Serializable object WorkoutListRoute
@Serializable object AnalysisRoute



//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//    val snackbarHostState = remember { SnackbarHostState() }
////        NavHost(
////            navController = navController,
////            startDestination = Login
////        ) {
////            composable<Login> { LoginScreen() }
////        }
//        Scaffold(
//            modifier = Modifier.fillMaxSize(),
//            snackbarHost = {
//                SnackbarHost(
//                    snackbarHostState,
//                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
//                )
//            }
//        ) { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = LoginRoute,
//                modifier = Modifier.padding(innerPadding)
//            ) {
//
//                composable<LoginRoute>{
//                    LoginScreen(
//                        navigateToAccountCreation = {
//                            navController.navigate(route = SignUpBaseRoute)
//                        },
//                        navigateToWorkoutList = {
//                            navController.navigate(route = WorkoutBaseRoute)
//                        },
//                        onShowSnackbar = { message, action ->
//                            snackbarHostState.showSnackbar(
//                                message = message,
//                                actionLabel = action,
//                                duration = SnackbarDuration.Short,
//                            ) == SnackbarResult.ActionPerformed
//                        }
//                    )
//                }
//
//                signUpGraph(
//                    navController = navController
//                )
//
//                workoutGraph(
//                    navController = navController
//                )
//
//                feedSection()
//
//
//                /*composable(route = Screens.Workout.name) {
//                    val workoutViewModel: WorkoutViewModel = viewModel()
//                    WorkoutListScreen(
//                        navController = navController,
//                        workoutViewModel = workoutViewModel
//                    )
//                }
//
//                composable(route = Screens.WorkoutDetails.name) {
//                    WorkoutDetailsScreen(
//                        workoutViewModel = viewModel()
//                    )
//
//                }*/
//
//
//        }
//    }
//}

fun NavGraphBuilder.loginScreen(
    navController: NavController,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    loginUser: () -> Unit,
) {
    composable<LoginRoute>{
        LoginScreen(
            navigateToAccountCreation = {
                navController.navigate(route = SignUpBaseRoute)
            },
            navigateToHome = {
                navController.navigate(route = FeedBaseRoute){
                    popUpTo<LoginRoute>{
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            onShowSnackbar = onShowSnackbar,
            signinUser = loginUser
        )
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
                    navController.navigate(route = Screens.LogIn.name)
                }
            )
        }
    }
}

fun NavController.navigateToWorkout(navOptions: NavOptions) =
    navigate(route = WorkoutListRoute, navOptions)

fun NavGraphBuilder.workoutGraph(
    navController: NavController
) {
    navigation<WorkoutBaseRoute>(
        startDestination = WorkoutListRoute
    )
    {
        composable<WorkoutListRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }


            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)


            WorkoutListScreen(
                workoutViewModel = workoutViewModel,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(Screens.WorkoutDetails.name) }
            )
        }


        composable(route = Screens.WorkoutDetails.name) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }


            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)
            WorkoutDetailsScreen(
                workoutViewModel = workoutViewModel,
                navUp = { navController.navigateUp() },
                        navNext = { navController.navigate(route = WorkoutListRoute) },
            )
        }
    }
}

fun NavController.navigateToFeed(navOptions: NavOptions) = navigate(route = FeedRoute, navOptions)

fun NavGraphBuilder.feedSection(){
    navigation<FeedBaseRoute>(startDestination = FeedRoute){
        composable<FeedRoute>(){
            FeedScreen()
        }
    }
}

fun NavController.navigateToAnalysis(navOptions: NavOptions) =
    navigate(route = AnalysisRoute, navOptions)

fun NavGraphBuilder.analysisScreen(){
    composable<AnalysisRoute>{
        AnalysisScreen()
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
