package com.example.datalift.navigation


import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.datalift.screens.analysis.AnalysisRoute
import com.example.datalift.screens.feed.FeedScreen
import com.example.datalift.screens.feed.FeedViewModel
import com.example.datalift.screens.feed.PostScreen
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.settings.SettingsDialogScreen
import com.example.datalift.screens.settings.SettingsScreen
import com.example.datalift.screens.settings.SettingsType
import com.example.datalift.screens.settings.SettingsViewModel
import com.example.datalift.screens.signUp.CredentialsScreen
import com.example.datalift.screens.signUp.NameScreen
import com.example.datalift.screens.signUp.PersonalInformationScreen
import com.example.datalift.screens.signUp.SignUpViewModel
import com.example.datalift.screens.workout.WorkoutDetailsScreen
import com.example.datalift.screens.workout.WorkoutListScreen
import com.example.datalift.screens.workout.WorkoutScreen
import com.example.datalift.screens.workout.WorkoutViewModel
import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object FeedBaseRoute
@Serializable object FeedRoute
@Serializable object SettingsBaseRoute
@Serializable object SettingsRoute
@Serializable object SignUpBaseRoute
@Serializable object NameRoute
@Serializable object PersonalInformationRoute
@Serializable object CredentialsRoute
@Serializable object WorkoutBaseRoute
@Serializable object WorkoutListRoute
@Serializable object AnalysisRoute

@Serializable data class WorkoutDetail(val id: String)
@Serializable data class SettingDetail(
    val title: String,
    val options: List<String>,
    val type: SettingsType
)
@Serializable data class PostDetail(
    val postId: String
)


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
                    navController.navigate(route = LoginRoute){
                        popUpTo<LoginRoute>{
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

fun NavController.navigateToWorkout(navOptions: NavOptions) =
    navigate(route = WorkoutListRoute, navOptions)

fun NavController.navigateToWorkoutDetail(id: String){
    navigate(route = WorkoutDetail(id))
}

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
                onWorkoutClick = navController::navigateToWorkoutDetail,
//                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(Screens.WorkoutDetails.name) }
            )
        }

        composable<WorkoutDetail> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }
            val workoutDetail: WorkoutDetail = backStackEntry.toRoute()

            val workoutViewModel: WorkoutViewModel = viewModel(parentEntry)
            workoutViewModel.getWorkout(workoutDetail.id)
            val workout = workoutViewModel.workout.collectAsStateWithLifecycle().value
            WorkoutScreen(
                workout = workout,
                navUp = { navController.navigateUp() }
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

fun NavController.navigateToPost(id: String){
    navigate(route = PostDetail(id))
}

fun NavGraphBuilder.feedSection(
    navController: NavController
){
    navigation<FeedBaseRoute>(startDestination = FeedRoute){
        composable<FeedRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = FeedBaseRoute)
            }

            val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)

            FeedScreen(
                feedViewModel = feedViewModel,
                navigateToPost = navController::navigateToPost
            )
        }

        composable<PostDetail> {  backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = FeedBaseRoute)
            }

            val postDetail: PostDetail = backStackEntry.toRoute()
            val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)

            feedViewModel.updateCurrentViewedPost(postDetail.postId)
            val currentPost = feedViewModel.currentPost.collectAsStateWithLifecycle().value
            PostScreen(
                navUp = { navController.navigateUp() },
                post = currentPost,
            )

        }
    }


}

fun NavController.navigateToAnalysis(navOptions: NavOptions) =
    navigate(route = AnalysisRoute, navOptions)

fun NavGraphBuilder.analysisScreen(){
    composable<AnalysisRoute>{
        AnalysisRoute()
    }
}

fun NavController.navigateToSettings() = navigate(route = SettingsBaseRoute)

fun NavController.navigateToSettingsDetail(
    settingDetail: SettingDetail
){
    navigate(route = settingDetail)
}

fun NavGraphBuilder.settingsSection(
    navController: NavController,
){
    navigation<SettingsBaseRoute>(startDestination = SettingsRoute){
        composable<SettingsRoute> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = SettingsBaseRoute)
            }

            val settingsViewModel: SettingsViewModel = hiltViewModel(parentEntry)

            SettingsScreen(
                settingsViewModel = settingsViewModel,
                onBackClick = navController::navigateUp,
                navigateToDetail = navController::navigateToSettingsDetail
            )
        }

        composable<SettingDetail> {backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = SettingsBaseRoute)
            }

            val settingDetail: SettingDetail = backStackEntry.toRoute()

            val settingsViewModel: SettingsViewModel = hiltViewModel(parentEntry)

            val uiState = settingsViewModel.uiState.collectAsStateWithLifecycle().value

            SettingsDialogScreen(
                navUp = navController::navigateUp,
                setting = settingDetail,
                uiState = uiState,
                getChoice = settingsViewModel.getCurrentChoiceUiState,
                updateChoice = settingsViewModel.updateFunction(settingDetail.type),
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
