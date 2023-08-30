package id.worx.device.client.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.worx.device.client.R

//val MontserratFontFamily = FontFamily(
//    listOf(
//        Font(R.font.montserrat_regular),
//        Font(R.font.montserrat_medium, FontWeight.Medium),
//        Font(R.font.montserrat_semibold, FontWeight.SemiBold)
//    )
//)

val fontRoboto = FontFamily(
    Font(R.font.roboto)
)

val openSans = FontFamily(
    Font(R.font.opensans)
)

val Typography = Typography(
    //defaultFontFamily = MontserratFontFamily,
    h1 = TextStyle(
        fontWeight = FontWeight.W300,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.W300,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 30.sp,
        letterSpacing = 0.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.W700,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 0.15.sp
    ),
    // app bar title
    h6 = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        letterSpacing = 0.15.sp,
        fontFamily = fontRoboto,
        lineHeight = 30.sp
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        fontFamily = fontRoboto,
        letterSpacing = 0.15.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        fontFamily = fontRoboto,
        letterSpacing = 0.15.sp,
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        fontFamily = fontRoboto
    ),
    button = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = 0.15.sp,
        fontFamily = fontRoboto
    ),
    caption = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline = TextStyle(
        fontWeight = FontWeight.W600,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
)