package com.example.datalift.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.datalift.ui.DataliftAppState

@Composable
fun DataliftNavHost(
    appState: DataliftAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    loginUser: () -> Unit,
    logoutUser: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = LoginRoute,
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

        feedSection()

        analysisScreen()

        settingsSection(
            navController = navController
        )
    }
}