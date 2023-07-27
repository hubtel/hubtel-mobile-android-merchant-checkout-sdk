package com.hubtel.sdk.checkout.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.sdk.checkout.platform.analytics.events.AnalyticsEvent
import com.hubtel.sdk.checkout.platform.analytics.events.EventParams

object EmptyEvent : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.Other.rawValue

    override val mixPanelEventParams: EventParams
        get() = mapOf()
}