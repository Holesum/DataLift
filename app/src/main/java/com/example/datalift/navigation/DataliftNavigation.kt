package com.example.datalift.navigation


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.example.datalift.screens.analysis.AnalysisRoute
import com.example.datalift.screens.challenges.ChallengeCreationScreen
import com.example.datalift.screens.challenges.ChallengeDetailScreen
import com.example.datalift.screens.challenges.ChallengesScreen
import com.example.datalift.screens.challenges.ChallengesViewModel
import com.example.datalift.screens.feed.FeedScreen
import com.example.datalift.screens.feed.FeedViewModel
import com.example.datalift.screens.feed.PostScreen
import com.example.datalift.screens.friends.FriendsScreen
import com.example.datalift.screens.logIn.LoginScreen
import com.example.datalift.screens.profile.ProfileScreen
import com.example.datalift.screens.profile.ProfileViewModel
import com.example.datalift.screens.settings.SettingsDialogScreen
import com.example.datalift.screens.settings.SettingsScreen
import com.example.datalift.screens.settings.SettingsType
import com.example.datalift.screens.settings.SettingsViewModel
import com.example.datalift.screens.signUp.CredentialsScreen
import com.example.datalift.screens.signUp.NameScreen
import com.example.datalift.screens.signUp.PersonalInformationScreen
import com.example.datalift.screens.signUp.SignUpViewModel
import com.example.datalift.screens.workout.WorkoutDetailsScreen
import com.example.datalift.screens.workout.WorkoutDetailsEditScreen
import com.example.datalift.screens.workout.WorkoutListScreen
import com.example.datalift.screens.workout.WorkoutScreen
import com.example.datalift.screens.workout.WorkoutViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.Serializable

@Serializable object LoginRoute
@Serializable object FeedBaseRoute
@Serializable object FeedRoute
@Serializable object FriendsRoute
@Serializable object SettingsBaseRoute
@Serializable object SettingsRoute
@Serializable object SignUpBaseRoute
@Serializable object NameRoute
@Serializable object PersonalInformationRoute
@Serializable object CredentialsRoute
@Serializable object WorkoutBaseRoute
@Serializable object WorkoutListRoute
@Serializable object AnalysisRoute
@Serializable object ProfileBaseRoute
@Serializable object ChallengesBaseRoute
@Serializable object ChallengesFeed
@Serializable object ChallengesCreation

@Serializable data class ChallengeDetail(val id: String)
@Serializable data class WorkoutDetail(val id: String)
@Serializable data class WorkoutDetailEdit(val id: String)
@Serializable data class SettingDetail(
    val title: String,
    val options: List<String>,
    val type: SettingsType
)
@Serializable data class PostDetail(
    val postId: String,
    val uid: String
)
@Serializable data class ProfileDetail(
    val profileId: String
)

fun getCurrentUserId(): String{
    val auth: FirebaseAuth = Firebase.auth
    val uid: String = auth.currentUser?.uid.toString()

    return uid
}

fun signOutUser(){
    val auth: FirebaseAuth = Firebase.auth
    auth.signOut()
}

fun openProfileDetails(context: Context, profileId: String) : PendingIntent{
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        "$uri/profile/$profileId".toUri(),
    )

    deepLinkIntent.setPackage(context.packageName)

    return PendingIntent.getActivity(
        context,
        0,
        deepLinkIntent,
        PendingIntent.FLAG_IMMUTABLE
    )
}

const val uri = "https://www.datalift.com"

fun NavController.navigateToLogin(){
    navigate(
        route = LoginRoute,
        navOptions = navOptions {
            popUpTo(0) {
                inclusive = true
            }
        }
    )
}

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

fun NavController.navigateToWorkoutDetailEdit(id: String){
    navigate(route = WorkoutDetailEdit(id))
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


            val workoutViewModel: WorkoutViewModel = hiltViewModel(parentEntry)


            WorkoutListScreen(
                workoutViewModel = workoutViewModel,
                onWorkoutClick = navController::navigateToWorkoutDetail,
                onWorkoutEditClick = navController::navigateToWorkoutDetailEdit,
//                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(Screens.WorkoutDetails.name) }
            )
        }

        composable<WorkoutDetail> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }
            val workoutDetail: WorkoutDetail = backStackEntry.toRoute()

            val workoutViewModel: WorkoutViewModel = hiltViewModel(parentEntry)
            val isImperial = workoutViewModel.getUnitSystem()
            workoutViewModel.getWorkout(workoutDetail.id)
            val workout = workoutViewModel.workout.collectAsStateWithLifecycle().value
            WorkoutScreen(
                workout = workout,
                navUp = { navController.navigateUp() },
                isImperial = isImperial
            )
        }



        composable(route = Screens.WorkoutDetails.name) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }


            val workoutViewModel: WorkoutViewModel = hiltViewModel(parentEntry)
            WorkoutDetailsScreen(
                workoutViewModel = workoutViewModel,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(route = WorkoutListRoute) },

            )
        }

        composable<WorkoutDetailEdit> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = WorkoutBaseRoute)
            }
            val workoutDetailEdit: WorkoutDetailEdit = backStackEntry.toRoute()

            val workoutViewModel: WorkoutViewModel = hiltViewModel(parentEntry)
            workoutViewModel.getWorkout(workoutDetailEdit.id)
            val workout = workoutViewModel.workout.collectAsStateWithLifecycle().value
            WorkoutDetailsEditScreen(
                workoutViewModel = workoutViewModel,
                workout = workout,
                navUp = { navController.navigateUp() },
                navNext = { navController.navigate(route = WorkoutListRoute) },
                removeSet = workoutViewModel::removeSet

                )
        }
    }
}

fun NavController.navigateToFeed(navOptions: NavOptions) = navigate(route = FeedRoute, navOptions)

fun NavController.navigateToPost(id: String, uid: String){
    navigate(route = PostDetail(id, uid))
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
                navigateToPost = navController::navigateToPost,
                navigateToProfile = navController::navigateToProfile
            )
        }

        composable<PostDetail> {  backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(route = FeedBaseRoute)
            }

            val postDetail: PostDetail = backStackEntry.toRoute()
            val feedViewModel: FeedViewModel = hiltViewModel(parentEntry)
            val isImperial = feedViewModel.getUnitSystem()
            feedViewModel.updateCurrentViewedPost(postDetail.postId, postDetail.uid)
            val currentPost = feedViewModel.currentPost.collectAsStateWithLifecycle().value
            PostScreen(
                navUp = { navController.navigateUp() },
                navigateToProfile = navController::navigateToProfile,
                post = currentPost,
                isImperial = isImperial,
                addLike = feedViewModel::addLike
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
    logoutUser: () -> Unit,
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
                navigateToDetail = navController::navigateToSettingsDetail,
                signOutUser = {
                    logoutUser()
                    signOutUser()
                    navController.navigateToLogin()
                }
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

fun NavController.navigateToFriends() = navigate(route = FriendsRoute)

fun NavGraphBuilder.friendsRoute(
    navUp: () -> Unit,
    navigationToProfile: (String) -> Unit,
){
    composable<FriendsRoute>{
        FriendsScreen(
            navUp = navUp,
            navigateToProfile = navigationToProfile
        )
    }
}

fun NavController.navigateToProfile(
    profileId: String = getCurrentUserId()
) = navigate(route = ProfileDetail(profileId = profileId))

fun NavGraphBuilder.profileRoute(
    navUp: () -> Unit,
) {
    navigation<ProfileBaseRoute>(startDestination = ProfileDetail(getCurrentUserId())){
        composable<ProfileDetail>(
            deepLinks = listOf(
                navDeepLink<ProfileDetail>(
                    basePath = "$uri/profile"
                )
                // {$uri}/profile?profileId={$profileId}
                // {$uri}/profile/{$profileId}
            )
        ){ backStackEntry ->
            val profileViewModel: ProfileViewModel = hiltViewModel()

            ProfileScreen(
                profileViewModel = profileViewModel,
                navUp = navUp
            )


        }
    }
}


fun NavController.navigateToChallengesFeed(navOptions: NavOptions) =
    navigate(route = ChallengesFeed, navOptions)

fun NavController.navigateToChallenge(id: String) = navigate(ChallengeDetail(id))

fun NavController.navigateToChallengeCreation() = navigate(ChallengesCreation)

fun NavGraphBuilder.challengesRoute(
    navUp: () -> Unit,
    getBackStackEntry: (ChallengesBaseRoute) -> NavBackStackEntry,
    navigateToChallengeFeed: (navOptions: NavOptions) -> Unit,
    navigateToChallenge: (String) -> Unit,
    navigateToChallengeCreation: () -> Unit,
    navigationToProfile: (String) -> Unit
) {
    navigation<ChallengesBaseRoute>(startDestination = ChallengesFeed){
       composable<ChallengesFeed> { backStackEntry ->
           val parentEntry = remember(backStackEntry) {
               getBackStackEntry(ChallengesBaseRoute )
           }

           val challengesViewModel: ChallengesViewModel = hiltViewModel(parentEntry)

           ChallengesScreen(
               challengesViewModel = challengesViewModel,
               navigateToChallenge = navigateToChallenge,
               navigateToChallengeCreation = navigateToChallengeCreation
           )
       }

        composable<ChallengeDetail> {  backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                getBackStackEntry(ChallengesBaseRoute )
            }

            val challengeDetail: ChallengeDetail = backStackEntry.toRoute()
            val challengesViewModel: ChallengesViewModel = hiltViewModel(parentEntry)
            challengesViewModel.loadChallenge(challengeDetail.id)
            val challenge by challengesViewModel.currentChallenge.collectAsStateWithLifecycle()
//            val challenge by challengesViewModel.RetrieveChallenge(challengeDetail.id).collectAsStateWithLifecycle()
            val error: Boolean = (challenge == null)

            ChallengeDetailScreen(
                challenge = challenge,
                navigateUp = navUp,
                currentUser = getCurrentUserId(),
                error = error
            )
        }

        composable<ChallengesCreation> { backstackEntry ->
            ChallengeCreationScreen(
                navUp = navUp,
                navigateToChallengeFeed = {
                    navigateToChallengeFeed(
                        navOptions {
                            popUpTo<ChallengesFeed>{
                                inclusive = true
                            }
                        }
                    )
                },
                navigateToProfile = navigationToProfile
            )
        }
    }
}