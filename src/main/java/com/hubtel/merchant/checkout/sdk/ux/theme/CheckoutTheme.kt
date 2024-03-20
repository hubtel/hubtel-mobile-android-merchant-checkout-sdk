package com.hubtel.merchant.checkout.sdk.ux.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color




class CheckoutColors(
    colorPrimary: Color,
    colorAccent: Color,
) {
    var colorPrimary by mutableStateOf(colorPrimary)
        private set

    var colorAccent by mutableStateOf(colorAccent)
        private set

    fun update(colors: CheckoutColors) {
        colorPrimary = colors.colorPrimary
        colorAccent = colors.colorAccent
    }
}

private val LightColorPalette = CheckoutColors(
    colorPrimary = TealPrimary,
    colorAccent = Teal100
)

private val LocalCheckoutColors = staticCompositionLocalOf<CheckoutColors> {
    error("No HubtelColorPalette provided")
}

@Composable
internal fun ProvideCheckoutColors(
    colors: CheckoutColors = LightColorPalette,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorPalette = remember { colors }
    colorPalette.update(colors)

    CompositionLocalProvider(
        LocalCheckoutColors provides colors,
        content = content
    )
}


object CheckoutTheme {
    val colors: CheckoutColors
        @Composable
        get() = LocalCheckoutColors.current
}