package com.darkyellowstudios.localflix.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Sora and Inter would ideally be loaded as resources. 
// For now, using default FontFamilies as placeholders if actual fonts aren't available.
val Sora = FontFamily.SansSerif
val Inter = FontFamily.SansSerif
val JetBrainsMono = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Sora,
        fontSize = 40.sp,
        fontWeight = FontWeight.W800,
        lineHeight = 48.sp,
        letterSpacing = (-0.03).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Sora,
        fontSize = 32.sp,
        fontWeight = FontWeight.W700,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Sora,
        fontSize = 24.sp,
        fontWeight = FontWeight.W700,
        lineHeight = 32.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontSize = 18.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 28.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontSize = 16.sp,
        fontWeight = FontWeight.W400,
        lineHeight = 24.sp
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 12.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 16.sp,
        letterSpacing = 0.08.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMono,
        fontSize = 10.sp,
        fontWeight = FontWeight.W500,
        lineHeight = 14.sp,
        letterSpacing = 0.1.sp
    )
)
