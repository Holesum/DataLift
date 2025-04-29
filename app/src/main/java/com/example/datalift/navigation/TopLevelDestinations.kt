package com.example.datalift.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.datalift.R
import com.example.datalift.ui.components.DataliftIcons
import kotlin.reflect.KClass


enum class TopLevelDestinations(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    FEED(
        selectedIcon = DataliftIcons.Home,
        unselectedIcon = DataliftIcons.HomeBorder,
        iconTextId = R.string.feed_title,
        route = FeedRoute::class
    ),
    WORKOUTS(
        selectedIcon = DataliftIcons.Edit,
        unselectedIcon = DataliftIcons.EditBorder,
        iconTextId = R.string.workouts_title,
        route = WorkoutListRoute::class,
        baseRoute = WorkoutBaseRoute::class
    ),
    ANALYSIS(
        selectedIcon = DataliftIcons.Magnifier,
        unselectedIcon = DataliftIcons.MagnifierBorder,
        iconTextId = R.string.analysis_title,
        route = AnalysisRoute::class
    ),
    CHALLENGES(
        selectedIcon = DataliftIcons.Trophy,
        unselectedIcon = DataliftIcons.TrophyOutlined,
        iconTextId = R.string.challenges_title,
        route = ChallengesFeed::class,
        baseRoute = ChallengesBaseRoute::class
    )
}