package com.hubtel.merchant.checkout.sdk.platform.analytics.constants

internal enum class AnalyticsProvider {
    FIREBASE,
    MIX_PANEL,
    EVENT_STORE,
    APPS_FLYER;

    companion object {
        @JvmStatic
        val DEFAULT: List<AnalyticsProvider> = listOf(
            FIREBASE,
            MIX_PANEL,
            EVENT_STORE
        )
    }
}