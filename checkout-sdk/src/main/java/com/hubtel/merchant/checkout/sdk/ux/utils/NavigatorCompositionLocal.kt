package com.hubtel.merchant.checkout.sdk.ux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.hubtel.merchant.checkout.sdk.ux.navigation.DeeplinkNavigator
import com.hubtel.merchant.checkout.sdk.ux.shared.BaseAppNavigator

val LocalDeeplinkNavigator = staticCompositionLocalOf<DeeplinkNavigator?> {
    noLocalProvidedFor("DeeplinkNavigator")
}

@Composable
internal fun ProvideDeeplinkNavigator(
    navigator: DeeplinkNavigator?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDeeplinkNavigator provides navigator,
        content = content
    )
}

val LocalAppNavigator = staticCompositionLocalOf<BaseAppNavigator?> {
    noLocalProvidedFor("BaseAppNavigator")
}

@Composable
internal fun ProvideAppNavigator(
    navigator: BaseAppNavigator?,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppNavigator provides navigator,
        content = content
    )
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}
