package com.hubtel.sdk.checkout.platform.analytics

import com.hubtel.core_analytics.events.sections.CheckoutEvent
import com.hubtel.core_analytics.extensions.toEvent
import com.hubtel.sdk.checkout.platform.analytics.events.types.ApiRequestEvent
import com.hubtel.sdk.checkout.platform.analytics.events.types.BeginPurchaseEvent
import com.hubtel.sdk.checkout.platform.analytics.events.types.PurchaseEvent
import com.hubtel.sdk.checkout.platform.analytics.events.types.PurchaseFailedEvent


fun recordCheckoutEvent(event: CheckoutEvent) {
    AnalyticsUtils.recordAnalyticsEvent(event.toEvent())
}

fun recordPurchaseEvent(event: PurchaseEvent) {
    AnalyticsUtils.recordAnalyticsEvent(event)
}

fun recordBeginPurchaseEvent(event: BeginPurchaseEvent) {
    AnalyticsUtils.recordAnalyticsEvent(event)
}

fun recordPurchaseFailedEvent(event: PurchaseFailedEvent) {
    AnalyticsUtils.recordAnalyticsEvent(event)
}

fun recordApiRequestEvent(event: ApiRequestEvent) {
    AnalyticsUtils.recordAnalyticsEvent(event)
}