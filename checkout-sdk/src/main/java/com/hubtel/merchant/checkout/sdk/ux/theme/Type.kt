package com.hubtel.merchant.checkout.sdk.ux.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hubtel.merchant.checkout.sdk.R

internal val AppFontFamily = FontFamily(
    listOf(
        Font(
            R.font.nunitosans_regular,
            weight = FontWeight.Thin,
        ),
        Font(
            R.font.nunitosans_regular,
            weight = FontWeight.ExtraLight,
        ),
        Font(
            R.font.nunitosans_regular,
            weight = FontWeight.Light,
        ),
        Font(
            R.font.nunitosans_regular,
            weight = FontWeight.Normal,
        ),
        Font(
            R.font.nunitosans_regular,
            weight = FontWeight.Medium,
        ),
        Font(
            R.font.nunitosans_extrabold,
            weight = FontWeight.SemiBold,
        ),
        Font(
            R.font.nunitosans_extrabold,
            weight = FontWeight.Bold,
        ),
        Font(
            R.font.nunitosans_extrabold,
            weight = FontWeight.ExtraBold,
        ),
        Font(
            R.font.nunitosans_extrabold,
            weight = FontWeight.Black,
        ),
    )
)

@Deprecated(
    "Use HubtelTheme.typography instead",
    ReplaceWith(
        "HubtelTheme.typography",
        "com.hubtel.core_ui.theme.HubtelTheme",
    ),
)
val AppTypography = Typography(
    defaultFontFamily = AppFontFamily,

    h1 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 28.sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 20.sp
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 16.sp
    ),
    h4 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 14.sp
    ),
    h5 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 12.sp
    ),
    h6 = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 10.sp
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 14.sp
    ),
    button = TextStyle(
        fontWeight = FontWeight.Bold, fontSize = 14.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal, fontSize = 12.sp
    ),
)