package com.datalift.designsystem

import androidx.activity.ComponentActivity
import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.datalift.screenshot_testing.DefaultRoborazziOptions
import com.datalift.screenshot_testing.captureMultiTheme
import com.datalift.designsystem.components.DataliftLoadingIcon
import com.datalift.designsystem.theme.DataliftTheme
import com.github.takahirom.roborazzi.captureRoboImage
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(application = HiltTestApplication::class, qualifiers = "480dpi")
@LooperMode(LooperMode.Mode.PAUSED)
class LoadingIconScreenshotTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun loadingIcon_mutlipleThemes() {
        composeTestRule.captureMultiTheme("LoadingIcon"){
            Surface {
                DataliftLoadingIcon(contentDesc = "test")
            }
        }
    }

    @Test
    fun loadingIconAnimation(){
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            DataliftTheme {
                DataliftLoadingIcon(contentDesc = "")
            }
        }

        listOf(20L, 115L, 724L, 1000L).forEach { deltaTime ->
            composeTestRule.mainClock.advanceTimeBy(deltaTime)
            composeTestRule.onRoot()
                .captureRoboImage("src/test/screenshots/LoadingIcon/LoadingIcon_animation_$deltaTime.png",
                    roborazziOptions = DefaultRoborazziOptions
                )
        }
    }
}