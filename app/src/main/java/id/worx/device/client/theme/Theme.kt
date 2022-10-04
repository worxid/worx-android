package id.worx.device.client.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightThemeColors = lightColors(
    primary = PrimaryMain,
    primaryVariant = RedDark,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = RedDark,
    onError = Color.White
)

val DarkThemeColors = darkColors(
    primary = PrimaryMain,
    primaryVariant = RedDark,
    onPrimary = Color.Black,
    secondary = Color.Black,
    onSecondary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    error = RedDark,
    onError = Color.Black
)


@Composable
fun WorxTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkThemeColors
    } else {
        LightThemeColors
    }
    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}


@Composable
fun WorxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = "System default",
    content: @Composable() () -> Unit
) {
    val colors = when (theme) {
        "System default" -> {
            val color = if (darkTheme) {
                DarkThemeColors
            } else {
                LightThemeColors
            }
            color
        }
        "Dark" -> LightThemeColors
        "Green" -> DarkThemeColors
        "Blue" -> LightThemeColors
        else -> DarkThemeColors
    }

    Log.d("TAG", "WorxTheme: ${theme}")

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}