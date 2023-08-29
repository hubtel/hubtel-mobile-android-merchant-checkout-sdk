package com.hubtel.merchant.checkout.sdk.ux.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorPalette = HubtelColors(
    colorPrimary = TealPrimary,
    colorOnPrimary = Color.White,
    colorSecondary = DarkBlue,
    colorOnSecondary = Color.White,
    uiBackground = GreyShade100,
    uiBackground2 = Color.White,
    uiBackground3 = Teal100,
    cardBackground = Color.White,
    textPrimary = Color.Black,
    textSecondary = Grey,
    outline = GreyShade300,
    outlineDarker = Grey,
    buttonLight = GreyShade100,
    buttonDisabled = GreyShade200,
    textDisabled = GreyShade700,
    textHint = GreyShade700,
    inputBackground = GreyShade300,
    error = Red,
    isDark = false
)

private val DarkColorPalette = HubtelColors(
    colorPrimary = TealPrimary,
    colorOnPrimary = Color.White,
    colorSecondary = DarkBlue,
    colorOnSecondary = Color.White,
    uiBackground = GreyShade100,
    uiBackground2 = Color.White,
    uiBackground3 = Teal100,
    cardBackground = Color.White,
    textPrimary = Color.Black,
    textSecondary = Grey,
    outline = GreyShade300,
    outlineDarker = Grey,
    buttonLight = GreyShade100,
    buttonDisabled = GreyShade200,
    textDisabled = GreyShade700,
    textHint = GreyShade700,
    inputBackground = GreyShade300,
    error = Red,
    isDark = true
)

@Composable
fun HubtelTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    ProvideHubtelColors(colors = colors) {
        MaterialTheme(
            colors = debugColor(darkTheme),
            typography = AppTypography,
            shapes = Shapes,
            content = content
        )
    }
}

object HubtelTheme {
    val colors: HubtelColors
        @Composable
        get() = LocalHubtelColors.current

    val shapes: Shapes
        @Composable
        get() = MaterialTheme.shapes

    val typography: Typography
        @Composable
        get() = MaterialTheme.typography
}


class HubtelColors(
    colorPrimary: Color,
    colorOnPrimary: Color,
    colorSecondary: Color,
    colorOnSecondary: Color,
    uiBackground: Color,
    uiBackground2: Color,
    uiBackground3: Color,
    cardBackground: Color,
    textPrimary: Color,
    textSecondary: Color,
    outline: Color,
    outlineDarker: Color,
    buttonLight: Color,
    buttonDisabled: Color,
    textDisabled: Color,
    textHint: Color,
    inputBackground: Color,
    error: Color,
    isDark: Boolean,
) {

    var colorPrimary by mutableStateOf(colorPrimary)
        private set

    var colorOnPrimary by mutableStateOf(colorOnPrimary)
        private set

    var colorSecondary by mutableStateOf(colorSecondary)
        private set

    var colorOnSecondary by mutableStateOf(colorOnSecondary)
        private set

    var uiBackground by mutableStateOf(uiBackground)
        private set

    var uiBackground2 by mutableStateOf(uiBackground2)
        private set

    var uiBackground3 by mutableStateOf(uiBackground3)
        private set

    var cardBackground by mutableStateOf(cardBackground)
        private set

    var textPrimary by mutableStateOf(textPrimary)
        private set

    var textSecondary by mutableStateOf(textSecondary)
        private set

    var outline by mutableStateOf(outline)
        private set

    var outlineDarker by mutableStateOf(outlineDarker)
        private set

    var buttonLight by mutableStateOf(buttonLight)
        private set

    var buttonDisabled by mutableStateOf(buttonDisabled)
        private set

    var textDisabled by mutableStateOf(textDisabled)
        private set

    var textHint by mutableStateOf(textHint)
        private set

    var inputBackground by mutableStateOf(inputBackground)
        private set

    var error by mutableStateOf(error)
        private set

    var isDark by mutableStateOf(isDark)
        private set

    fun update(newColors: HubtelColors) {
        colorPrimary = newColors.colorPrimary
        colorOnPrimary = newColors.colorOnPrimary

        colorSecondary = newColors.colorSecondary
        colorOnSecondary = newColors.colorOnSecondary

        uiBackground = newColors.uiBackground
        uiBackground2 = newColors.uiBackground2
        uiBackground3 = newColors.uiBackground3

        cardBackground = newColors.cardBackground
        textPrimary = newColors.textPrimary
        textSecondary = newColors.textSecondary

        outline = newColors.outline
        outlineDarker = newColors.outlineDarker

        buttonLight = newColors.buttonLight
        buttonDisabled = newColors.buttonDisabled
        textDisabled = newColors.textDisabled
        textHint = newColors.textHint
        inputBackground = newColors.inputBackground
        error = newColors.error

        isDark = newColors.isDark
    }
}


@Composable
internal fun ProvideHubtelColors(
    colors: HubtelColors,
    content: @Composable () -> Unit
) {
    val colorPalette = remember { colors }
    colorPalette.update(colors)
    CompositionLocalProvider(LocalHubtelColors provides colorPalette, content = content)
}

private val LocalHubtelColors = staticCompositionLocalOf<HubtelColors> {
    error("No HubtelColorPalette provided")
}


/* Discourage attempt to reference material colors
* by producing teal green for everything*/
private fun debugColor(
    darkTheme: Boolean,
    debugColor: Color = TealPrimary
) = Colors(
    primary = debugColor,
    primaryVariant = debugColor,
    secondary = debugColor,
    secondaryVariant = debugColor,
    background = debugColor,
    surface = debugColor,
    error = debugColor,
    onPrimary = debugColor,
    onSecondary = debugColor,
    onBackground = debugColor,
    onSurface = debugColor,
    onError = debugColor,
    isLight = !darkTheme
)