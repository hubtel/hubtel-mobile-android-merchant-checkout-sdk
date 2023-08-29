package com.hubtel.merchant.checkout.sdk.ux.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.hubtel.merchant.checkout.sdk.ux.shared.BaseActivity

val LocalActivity = staticCompositionLocalOf<BaseActivity> {
    noLocalProvidedFor("BaseActivity")
}

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

@Composable
internal fun ProvideBaseActivity(
    activity: BaseActivity,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalActivity provides activity,
        content = content
    )
}