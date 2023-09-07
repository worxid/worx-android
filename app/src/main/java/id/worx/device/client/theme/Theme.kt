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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import dagger.hilt.android.internal.managers.FragmentComponentManager
import id.worx.device.client.screen.main.AppTheme
import id.worx.device.client.screen.main.getAppTheme

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
    val splashBackground: Color = Color.Unspecified,
    val text: Color = Color.Unspecified,
    val subText: Color = Color.Unspecified,
    val textFieldContainer: Color = Color.Unspecified,
    val textFieldUnfocusedLabel: Color = Color.Unspecified,
    val textFieldColor: Color = Color.Unspecified,
    val textFieldFocusedLabel: Color = Color.Unspecified,
    val textFieldFocusedIndicator: Color = Color.Unspecified,
    val textFieldIcon: Color = Color.Unspecified,
    val textFieldUnfocusedIndicator: Color = Color.Unspecified,
    val button: Color = Color.Unspecified,
    val icon: Color = Color.Unspecified,
    val iconV2: Color = Color.Unspecified,
    val formItemContainer: Color = Color.Unspecified,
    val appBar: Color = Color.Unspecified,
    val iconBackground: Color = Color.Unspecified,
    val bottomSheetBackground: Color = Color.Unspecified,
    val bottomSheetDragHandle: Color = Color.Unspecified,
    val homeBackground: Color = Color.Unspecified,
    val bottomNavigationBorder: Color = Color.Unspecified,
    val divider: Color = Color.Unspecified,
    val optionBorder: Color = Color.Unspecified,
    val unselectedStar: Color = Color.Unspecified,
    val selectedStar: Color = Color.Unspecified,
    val documentBackground: Color = Color.Unspecified,
)

val WorxLightColorsPalette = WorxColorsPalette(
    splashBackground = PrimaryMain,
    text = Color.Black.copy(alpha = 0.87f),
    subText = Color.Black.copy(alpha = 0.6f),
    textFieldContainer = backgroundFormList,
    textFieldUnfocusedLabel = LightThemeColorsSystem.onSecondary.copy(alpha = 0.6f),
    textFieldColor = LightThemeColorsSystem.onSecondary.copy(alpha = 0.87f),
    textFieldFocusedLabel = PrimaryMain,
    textFieldFocusedIndicator = PrimaryMain,
    textFieldIcon = Color.Black.copy(alpha = 0.54f),
    textFieldUnfocusedIndicator = Color.Black.copy(alpha = 0.54f),
    button = PrimaryMain,
    icon = Silver,
    iconV2 = Silver,
    formItemContainer = Color.White,
    appBar = PrimaryMain,
    iconBackground = PrimaryMain,
    bottomSheetBackground = Color.White,
    bottomSheetDragHandle = DragHandle,
    homeBackground = backgroundFormList,
    bottomNavigationBorder = LightThemeColorsSystem.onSecondary,
    divider = Color.Black.copy(alpha = 0.12f),
    optionBorder = Color.Black.copy(alpha = 0.54f),
    unselectedStar = Color.Black.copy(alpha = 0.38f),
    selectedStar = Star,
    documentBackground = DragHandle,
)

val WorxDarkColorsPalette = WorxColorsPalette(
    splashBackground = Splash,
    text = Color.White.copy(alpha = 0.87f),
    subText = Color.White.copy(alpha = 0.6f),
    textFieldContainer = Shark2,
    textFieldUnfocusedLabel = DarkThemeColorsSystem.onSecondary.copy(alpha = 0.6f),
    textFieldColor = DarkThemeColorsSystem.onSecondary.copy(alpha = 0.87f),
    textFieldFocusedLabel = Cinnabar,
    textFieldFocusedIndicator = Cinnabar,
    textFieldIcon = Color.White.copy(alpha = 0.54f),
    textFieldUnfocusedIndicator = Color.White.copy(alpha = 0.54f),
    button = Cinnabar,
    icon = Boulder,
    iconV2 = Edward,
    formItemContainer = Shark,
    appBar = Shark,
    iconBackground = Pomegranate,
    bottomSheetBackground = CapeCod,
    bottomSheetDragHandle = Abbey,
    homeBackground = Shark2,
    bottomNavigationBorder = Abbey2,
    divider = Color.White.copy(alpha = 0.12f),
    optionBorder = Color.White.copy(alpha = 0.54f),
    unselectedStar = Color.White.copy(alpha = 0.38f),
    selectedStar = Star,
    documentBackground = Abbey,
)

val WorxCustomColorsPalette = compositionLocalOf { WorxColorsPalette() }

@Composable
fun WorxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    theme: String = AppTheme.LIGHT.value,
    content: @Composable() () -> Unit
) {
    val (colors, customColors, isLightTheme) = when (theme.getAppTheme()) {
        AppTheme.LIGHT -> Triple(LightThemeColorsSystem, WorxLightColorsPalette, true)
        AppTheme.DARK -> Triple(DarkThemeColorsSystem, WorxDarkColorsPalette, false)
        else ->
            if (darkTheme) {
                Triple(DarkThemeColorsSystem, WorxDarkColorsPalette, false)
            } else {
                Triple(LightThemeColorsSystem, WorxLightColorsPalette, true)
            }

    }

    CompositionLocalProvider(
        WorxCustomColorsPalette provides customColors
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

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLightTheme
        }
    }
}