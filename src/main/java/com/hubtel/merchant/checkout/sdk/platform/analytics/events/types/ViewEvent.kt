package com.hubtel.merchant.checkout.sdk.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AnalyticsConstants
import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.core_analytics.constants.UIType
import com.hubtel.core_utils.constants.AppSection
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams

internal class ViewEvent(
    private val pageName: String,
    private val section: AppSection,
    private val uiType: UIType,
    private val viewId: String,
    private val viewName: String,
    private val viewShortName: String,
) : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.View.rawValue

    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.View.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.PAGE_NAME to pageName,
                AnalyticsConstants.EventStoreParam.SECTION_NAME to section.rawValue,
                AnalyticsConstants.EventStoreParam.UI_TYPE to uiType.rawValue,
                AnalyticsConstants.EventStoreParam.VIEW_ID to viewId,
                AnalyticsConstants.EventStoreParam.VIEW_NAME to viewName,
                AnalyticsConstants.EventStoreParam.VIEW_SHORT_NAME to viewShortName,
            )
        )
}