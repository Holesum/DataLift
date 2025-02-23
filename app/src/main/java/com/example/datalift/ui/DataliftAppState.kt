package com.example.datalift.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.datalift.navigation.TopLevelDestinations
import com.example.datalift.navigation.TopLevelDestinations.ANALYSIS
import com.example.datalift.navigation.TopLevelDestinations.FEED
import com.example.datalift.navigation.TopLevelDestinations.WORKOUTS
import com.example.datalift.navigation.navigateToAnalysis
import com.example.datalift.navigation.navigateToFeed
import com.example.datalift.navigation.navigateToWorkout

@Composable
fun rememberDataliftAppState(
    navController: NavHostController = rememberNavController()
): DataliftAppState {
    return remember(
        navController,
    ) {
        DataliftAppState(
            navController
        )
    }
}


class DataliftAppState(
    val navController: NavHostController
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if(destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    val topLevelDestinations: List<TopLevelDestinations> = TopLevelDestinations.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestinations){
        trace("Navigation: ${topLevelDestination.name}"){
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id){
                    saveState = true
                }
                launchSingleTop = true
                restoreState= true
            }

            when(topLevelDestination){
                FEED -> navController.navigateToFeed(topLevelNavOptions)
                WORKOUTS -> navController.navigateToWorkout(topLevelNavOptions)
                ANALYSIS -> navController.navigateToAnalysis(topLevelNavOptions)
            }
        }
    }
}