package com.datalift.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = Orange80,
    tertiary = Blue20,
)

@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Blue40,
    surfaceVariant = PurpleGrey90,
    onSurfaceVariant = PurpleGrey60,
)

@VisibleForTesting
val LightAndroidColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@VisibleForTesting
val DarkAndroidColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightAndroidGradientColors = GradientColors(container = DarkGreenGray95)

val DarkAndroidGradientColors = GradientColors(container = Color.Black)

val LightAndroidBackgroundTheme = BackgroundTheme(color = DarkGreenGray95)

val DarkAndroidBackgroundTheme = BackgroundTheme(color = Color.Black)

@Composable
fun DataliftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    androidTheme: Boolean = false,
    disableDynamicThemeing: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        androidTheme -> if(darkTheme) DarkAndroidColorScheme else LightAndroidColorScheme
        !disableDynamicThemeing && supportDynamicTheming() -> {
            val context = LocalContext.current
            if(darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> if(darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }

    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface
    )
    val gradientColors = when {
        androidTheme -> if(darkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
        !disableDynamicThemeing && supportDynamicTheming() -> emptyGradientColors
        else -> defaultGradientColors
    }

    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }
    val tintTheme = when {
        androidTheme -> TintTheme()
        !disableDynamicThemeing && supportDynamicTheming() -> TintTheme(colorScheme.primary)
        else -> TintTheme()
    }

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = DataliftTypography,
            content = content
        )
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S