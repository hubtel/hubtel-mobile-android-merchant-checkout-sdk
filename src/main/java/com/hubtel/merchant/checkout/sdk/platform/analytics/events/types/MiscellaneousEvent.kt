package com.hubtel.merchant.checkout.sdk.platform.analytics.events.types

import com.hubtel.merchant.checkout.sdk.platform.analytics.constants.AnalyticsConstants
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams
import com.hubtel.core_utils.constants.AppSection

class MiscellaneousEvent(
    private val message: String,
    private val eventName: String,
    internal val section: AppSection
) : AnalyticsEvent() {

    override val firebaseEventName: String
        get() = eventName

    override val firebaseEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.Param.MESSAGE to message
        )

    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.PAGE_NAME to message,
                AnalyticsConstants.EventStoreParam.SECTION_NAME to section.rawValue
            )
        )
}