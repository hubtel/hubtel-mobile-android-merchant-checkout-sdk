package com.hubtel.merchant.checkout.sdk.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AnalyticsConstants
import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.core_utils.constants.AppSection
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams

class ViewItemEvent(
    private val itemId: String?,
    private val itemName: String,
    private val section: AppSection,
) : AnalyticsEvent() {


    override val mixpanelEventName: String
        get() = AppEventType.ViewItem.rawValue


    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.ViewItem.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.VIEW_ITEM_NAME to itemName,
                AnalyticsConstants.EventStoreParam.VIEW_ITEM_ID to itemId,
                AnalyticsConstants.EventStoreParam.SECTION_NAME to section.rawValue,
            )
        )
}