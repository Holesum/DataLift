@file:OptIn(ExperimentalRoborazziApi::class)
package com.datalift.screenshot_testing

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.datalift.designsystem.theme.DataliftTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage

val DefaultRoborazziOptions =
    RoborazziOptions(
        // Pixel-perfect matching
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
        // Reduce the size of the png
        recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5)
    )

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    overrideFileName: String? = null,
    shouldCompareDarkMode: Boolean = true,
    shouldCompareDynamicColor: Boolean = true,
    shouldCompareAndroidTheme: Boolean = true,
    content: @Composable (desc:String) -> Unit,
) {
    val darkModeValues = if(shouldCompareDarkMode) listOf(true, false) else listOf(false)
    val dynamicThemingValues = if(shouldCompareDynamicColor) listOf(true, false) else listOf(false)
    val androidThemeValues = if(shouldCompareAndroidTheme) listOf(true, false) else listOf(false)

    var darkMode by mutableStateOf(true)
    var dynamicTheming by mutableStateOf(false)
    var androidTheme by mutableStateOf(false)

    this.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true
        ) {
            DataliftTheme(
                androidTheme = androidTheme,
                darkTheme = darkMode,
                disableDynamicThemeing = !dynamicTheming,
            ) {
                key(androidTheme, darkMode, dynamicTheming) {
                    val description = generateDescription(
                        shouldCompareDarkMode,
                        darkMode,
                        shouldCompareAndroidTheme,
                        androidTheme,
                        shouldCompareDynamicColor,
                        dynamicTheming,
                    )
                    content(description)
                }
            }
        }
    }

    darkModeValues.forEach { isDarkMode ->
        darkMode = isDarkMode
        val darkModeDesc = if (isDarkMode) "dark" else "light"

        androidThemeValues.forEach { isAndroidTheme ->
            androidTheme = isAndroidTheme
            val androidThemeDesc = if (isAndroidTheme) "androidTheme" else "defaultTheme"

            dynamicThemingValues.forEach dyanmicTheme@{ isDynamicTheming ->
                if(isAndroidTheme && isDynamicTheming) return@dyanmicTheme

                dynamicTheming = isDynamicTheming
                val dynamicThemingDesc = if(isDynamicTheming) "dynamic" else "notDynamic"

                val filename = overrideFileName ?: name

                this.onRoot()
                    .captureRoboImage(
                        "src/test/screenshots/" +
                        "_$darkModeDesc" +
                        "_$androidThemeDesc" +
                        "_$dynamicThemingDesc" +
                        ".png",
                        roborazziOptions = DefaultRoborazziOptions,
                    )
            }
        }
    }
}

@Composable
private fun generateDescription(
    shouldCompareDarkMode: Boolean,
    darkMode: Boolean,
    shouldCompareAndroidTheme: Boolean,
    androidTheme: Boolean,
    shouldCompareDynamicColor: Boolean,
    dynamicThemeing: Boolean
): String {
    val description = "" +
            if(shouldCompareDarkMode) {
                if (darkMode) "Dark" else "Light"
            } else {
                ""
            } +
            if (shouldCompareAndroidTheme){
                if(androidTheme) " Android" else " Default"
            } else {
                ""
            } +
            if (shouldCompareDynamicColor){
                if(dynamicThemeing) " Dynamic" else ""
            } else {
                ""
            }

    return description.trim()
}