package id.worx.device.client.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import id.worx.device.client.screen.main.SettingTheme

//System default
@SuppressLint("ConflictingOnColor")
val LightThemeColorsSystem = lightColors(
    primary = PrimaryMain,
    primaryVariant = RedDark,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = PrimaryMain,
    onBackground = PrimaryMain,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = RedDark,
    onError = Color.White
)

@SuppressLint("ConflictingOnColor")
val DarkThemeColorsSystem = darkColors(
    primary = PrimaryMain,
    primaryVariant = RedDark,
    onPrimary = Color.White,
    secondary = backgroundFormList,
    onSecondary = Color.Black,
    background = PrimaryMain,
    onBackground = PrimaryMain,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = RedDark,
    onError = Color.White
)

//Dark
val LightThemeColorsDark = lightColors(
    primary = PrimaryMainDark,
    primaryVariant = BlackDark,
    onPrimary = Color.White,
    secondary = backgroundDark,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = PrimaryMain,
    surface = SurfaceMainDark,
    onSurface = Color.Black,
    error = BlackDark,
    onError = Color.White
)

val DarkThemeColorsDark = darkColors(
    primary = PrimaryMainDark,
    primaryVariant = BlackDark,
    onPrimary = Color.White,
    secondary = backgroundDark,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = PrimaryMain,
    surface = SurfaceMainDark,
    onSurface = Color.Black,
    error = BlackDark,
    onError = Color.White
)

//Green
@SuppressLint("ConflictingOnColor")
val LightThemeColorsGreen = lightColors(
    primary = PrimaryMainGreen,
    primaryVariant = GreenDark,
    onPrimary = Color.White,
    secondary = backgroundFormList,
    onSecondary = Color.Black,
    background = PrimaryMainGreen,
    onBackground = PrimaryMainGreen,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = GreenDark,
    onError = Color.White
)

@SuppressLint("ConflictingOnColor")
val DarkThemeColorsGreen = darkColors(
    primary = PrimaryMainGreen,
    primaryVariant = GreenDark,
    onPrimary = Color.White,
    secondary = backgroundFormList,
    onSecondary = Color.Black,
    background = PrimaryMainGreen,
    onBackground = PrimaryMainGreen,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = GreenDark,
    onError = Color.White
)

//Blue
@SuppressLint("ConflictingOnColor")
val LightThemeColorsBlue = lightColors(
    primary = PrimaryMainBlue,
    primaryVariant = BlueDark,
    onPrimary = Color.White,
    secondary = backgroundFormList,
    onSecondary = Color.Black,
    background = PrimaryMainBlue,
    onBackground = PrimaryMainBlue,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = BlueDark,
    onError = Color.White
)

@SuppressLint("ConflictingOnColor")
val DarkThemeColorsBlue = darkColors(
    primary = PrimaryMainBlue,
    primaryVariant = BlueDark,
    onPrimary = Color.White,
    secondary = backgroundFormList,
    onSecondary = Color.Black,
    background = PrimaryMainBlue,
    onBackground = PrimaryMainBlue,
    surface = SurfaceSystem,
    onSurface = Color.Black,
    error = BlueDark,
    onError = Color.White
)
@Composable
fun WorxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = SettingTheme.System,
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        when (theme) {
            SettingTheme.System -> DarkThemeColorsSystem
            SettingTheme.Dark -> DarkThemeColorsDark
            SettingTheme.Green -> DarkThemeColorsGreen
            SettingTheme.Blue -> DarkThemeColorsBlue
            else -> DarkThemeColorsSystem
        }
    } else {
        when (theme) {
            SettingTheme.System -> LightThemeColorsSystem
            SettingTheme.Dark -> LightThemeColorsDark
            SettingTheme.Green -> LightThemeColorsGreen
            SettingTheme.Blue -> LightThemeColorsBlue
            else -> LightThemeColorsSystem
        }
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}