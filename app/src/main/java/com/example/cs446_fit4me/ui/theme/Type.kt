package com.example.cs446_fit4me.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.example.cs446_fit4me.R
import androidx.compose.ui.text.font.FontVariation

// Define FontFamilies by referencing the font files in res/font
val Montserrat = FontFamily(
    Font(R.font.montserrat_black),
    Font(R.font.montserrat_blackitalic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserrat_extralight, FontWeight.ExtraLight),
    Font(R.font.montserrat_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_thin, FontWeight.Thin),
    Font(R.font.montserrat_thinitalic, FontWeight.Thin, FontStyle.Italic)
)

val Roboto = FontFamily(
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_blackitalic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_bolditalic, FontWeight.Bold, FontStyle.Italic),

    Font(R.font.roboto_condensed_black, FontWeight.Black),
    Font(R.font.roboto_condensed_blackitalic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.roboto_condensed_bold, FontWeight.Bold),
    Font(R.font.roboto_condensed_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.roboto_condensed_extrabold, FontWeight.ExtraBold),
    Font(R.font.roboto_condensed_extrabold, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.roboto_condensed_extralight, FontWeight.ExtraLight),
    Font(R.font.roboto_condensed_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.roboto_condensed_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_condensed_light, FontWeight.Light),
    Font(R.font.roboto_condensed_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.roboto_condensed_medium, FontWeight.Medium),
    Font(R.font.roboto_condensed_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.roboto_condensed_regular, FontWeight.Normal),
    Font(R.font.roboto_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.roboto_condensed_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.roboto_condensed_thin, FontWeight.Thin),
    Font(R.font.roboto_condensed_thinitalic, FontWeight.Thin, FontStyle.Italic),

    Font(R.font.roboto_extrabold, FontWeight.ExtraBold),
    Font(R.font.roboto_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.roboto_extralight, FontWeight.ExtraLight),
    Font(R.font.roboto_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),

    Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_semibold, FontWeight.SemiBold),
    Font(R.font.roboto_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),

    Font(R.font.roboto_semicondensed_black, FontWeight.Black),
    Font(R.font.roboto_semicondensed_blackitalic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_bold, FontWeight.Bold),
    Font(R.font.roboto_semicondensed_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_extrabold, FontWeight.ExtraBold),
    Font(R.font.roboto_semicondensed_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_extralight, FontWeight.ExtraLight),
    Font(R.font.roboto_semicondensed_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_light, FontWeight.Light),
    Font(R.font.roboto_semicondensed_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_medium, FontWeight.Medium),
    Font(R.font.roboto_semicondensed_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.roboto_semicondensed_regular, FontWeight.Normal),
    Font(R.font.roboto_semicondensed_semibold, FontWeight.SemiBold),
    Font(R.font.roboto_semicondensed_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),

    Font(R.font.roboto_thin, FontWeight.Thin),
    Font(R.font.roboto_thinitalic, FontWeight.Thin, FontStyle.Italic)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

val AppTypography = Typography(
    // Display Large - For very large, standout text (e.g., hero text on a splash, if any)
    displayLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    // Display Medium - Slightly smaller than display large
    displayMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    // Display Small - Smallest of the display styles
    displaySmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Normal, // Can be bold too, depends on desired emphasis
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline Large - For important screen titles
    headlineLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    // Headline Medium - Sub-headers or less prominent titles
    headlineMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // Headline Small - Smallest headline, good for section titles within content
    headlineSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title Large - For prominent titles within components (e.g., Card titles)
    titleLarge = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Title Medium - Standard titles within components
    titleMedium = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp // Slightly more spacing for titles
    ),
    // Title Small - Less prominent titles within components (e.g., subtitles)
    titleSmall = TextStyle(
        fontFamily = Montserrat,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body Large - Default body text size
    bodyLarge = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp // Standard letter spacing for body
    ),
    // Body Medium - Slightly smaller body text, for less emphasis or more dense info
    bodyMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    // Body Small - Smallest body text, for captions or tertiary information
    bodySmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label Large - For buttons and other interactive elements
    labelLarge = TextStyle(
        fontFamily = Roboto, // Or Montserrat for a slightly bolder button feel
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Label Medium - For smaller interactive elements or labels
    labelMedium = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Label Small - For very small labels, often metadata
    labelSmall = TextStyle(
        fontFamily = Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)