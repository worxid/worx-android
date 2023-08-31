package id.worx.device.client.theme

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dagger.hilt.android.internal.managers.FragmentComponentManager
import id.worx.device.client.screen.main.SettingTheme

//System default
@SuppressLint("ConflictingOnColor")
val LightThemeColorsSystem = lightColors(
    primary = PrimaryMain,
    primaryVariant = RedDark,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = PrimaryMain,
    surface = SurfaceSystem,
    onSurface = Silver,
    error = RedDark,
    onError = Color.White
)

@SuppressLint("ConflictingOnColor")
val DarkThemeColorsSystem = darkColors(
    primary = DarkBackground,
    primaryVariant = DarkPrimaryVariant,
    onPrimary = Color.White,
    secondary = DarkBackground,
    onSecondary = Color.White,
    background = PrimaryMain,
    onBackground = PrimaryMain,
    surface = SurfaceSystem,
    onSurface = DarkBackground,
    error = RedDark,
    onError = Color.White
)

@Immutable
data class WorxColorsPalette(
    val textFieldContainer: Color = Color.Unspecified,
    val textFieldUnfocusedLabel: Color = Color.Unspecified,
    val textFieldColor: Color = Color.Unspecified,
    val textFieldFocusedLabel: Color = Color.Unspecified,
    val textFieldFocusedIndicator: Color = Color.Unspecified,
    val button: Color = Color.Unspecified,
    val icon: Color = Color.Unspecified,
    val iconV2: Color = Color.Unspecified,
    val formItemContainer: Color = Color.Unspecified,
    val appBar: Color = Color.Unspecified,
    val iconBackground: Color = Color.Unspecified
)

val WorxLightColorsPalette = WorxColorsPalette(
    textFieldContainer = backgroundFormList,
    textFieldUnfocusedLabel = LightThemeColorsSystem.onSecondary.copy(alpha = 0.6f),
    textFieldColor = LightThemeColorsSystem.onSecondary.copy(alpha = 0.87f),
    textFieldFocusedLabel = PrimaryMain,
    textFieldFocusedIndicator = PrimaryMain,
    button = PrimaryMain,
    icon = Silver,
    iconV2 = Silver,
    formItemContainer = Color.White,
    appBar = PrimaryMain,
    iconBackground = PrimaryMain
)

val WorxDarkColorsPalette = WorxColorsPalette(
    textFieldContainer = MineShaft,
    textFieldUnfocusedLabel = DarkThemeColorsSystem.onSecondary.copy(alpha = 0.6f),
    textFieldColor = DarkThemeColorsSystem.onSecondary.copy(alpha = 0.87f),
    textFieldFocusedLabel = Cinnabar,
    textFieldFocusedIndicator = Cinnabar,
    button = Cinnabar,
    icon = Boulder,
    iconV2 = Edward,
    formItemContainer = Shark,
    appBar = Shark,
    iconBackground = Pomegranate
)

val LocalCustomColorsPalette = staticCompositionLocalOf { WorxColorsPalette() }

@Composable
fun WorxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = SettingTheme.System,
    content: @Composable() () -> Unit
) {
    val (colors, customColors) = if (darkTheme) {
        Pair(DarkThemeColorsSystem, WorxDarkColorsPalette)
    } else {
        Pair(DarkThemeColorsSystem, WorxDarkColorsPalette)
//        Pair(LightThemeColorsSystem, WorxLightColorsPalette)
    }

    CompositionLocalProvider(
        LocalCustomColorsPalette provides customColors
    ) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = FragmentComponentManager.findActivity(view.context) as Activity
            val window = activity.window
            window.statusBarColor = colors.primaryVariant.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }
}