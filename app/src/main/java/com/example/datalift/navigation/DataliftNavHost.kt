package com.example.datalift.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.datalift.ui.DataliftAppState

@Composable
fun DataliftNavHost(
    appState: DataliftAppState,
    modifier: Modifier = Modifier,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    userLoggedIn: Boolean = false,
    loginUser: () -> Unit,
    logoutUser: () -> Unit,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = if(userLoggedIn) FeedBaseRoute else LoginRoute,
        modifier = modifier
    ){
        loginScreen(
            navController = navController,
            onShowSnackbar = onShowSnackbar,
            loginUser = loginUser
        )

        signUpGraph(
            navController = navController
        )

        workoutGraph(
            navController = navController
        )

        feedSection(
            navController = navController
        )

        analysisScreen()

        settingsSection(
            navController = navController,
            logoutUser = logoutUser
        )

        friendsRoute(
            navUp = navController::navigateUp,
            navigationToProfile = navController::navigateToProfile
        )

        profileRoute(
            navUp = navController::navigateUp
        )

        challengesRoute(
            navUp = navController::navigateUp,
            getBackStackEntry = navController::getBackStackEntry,
            navigateToChallengeFeed = navController::navigateToChallengesFeed,
            navigateToChallenge = navController::navigateToChallenge,
            navigateToChallengeCreation = navController::navigateToChallengeCreation
        )
    }
}