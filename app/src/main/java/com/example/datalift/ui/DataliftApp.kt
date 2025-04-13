package com.example.datalift.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.datalift.navigation.DataliftNavHost
import com.example.datalift.navigation.navigateToFriends
import com.example.datalift.navigation.navigateToProfile
import com.example.datalift.navigation.navigateToSettings
import com.example.datalift.ui.components.DataliftNavigationBar
import com.example.datalift.ui.components.DataliftNavigationBarItem
import com.example.datalift.ui.components.DataliftTopBar
import kotlin.reflect.KClass

@Composable
fun DataliftApp(
    appState: DataliftAppState,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
//    var loggedIn = rememberSaveable { mutableStateOf(false) }

    DataliftApp(
        appState = appState,
        snackbarHostState = snackbarHostState,
//        loggedIn = loggedIn,
        modifier = modifier
    )
}

@Composable
internal fun DataliftApp(
    appState: DataliftAppState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val currentDestination = appState.currentDestination
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        },
        topBar = {
            if(appState.loggedIn){
                val destination = appState.currentTopLevelDestinations
                if(destination != null){
                    DataliftTopBar(
                        title = stringResource(destination.iconTextId),
                        onSettingsClick = appState.navController::navigateToSettings,
                        onFriendsClick = appState.navController::navigateToFriends,
                        onProfileClick = appState.navController::navigateToProfile
                    )
                }
            }
        },
        bottomBar = {
            if(appState.loggedIn){
                DataliftNavigationBar {
                    appState.topLevelDestinations.forEach {destination ->
                        val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                        DataliftNavigationBarItem(
                            onClick = { appState.navigateToTopLevelDestination(destination) },
                            selected = selected,
                            icon = {
                                Icon(
                                    imageVector = destination.unselectedIcon,
                                    contentDescription = null
                                )
                            },
                            selectedIcon = {
                                Icon(
                                    imageVector = destination.selectedIcon,
                                    contentDescription = null
                                )
                            },
                            label = { Text(stringResource(destination.iconTextId))}
                        )
                    }
                }
            }
        }

    ){ padding ->  
        DataliftNavHost(
            appState = appState,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == SnackbarResult.ActionPerformed
            },
            userLoggedIn = appState.loggedIn,
            loginUser = { appState.loggedIn = true },
            logoutUser = { appState.loggedIn = false },
            modifier = Modifier.padding(padding)
        )
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route = route)
    } ?: false