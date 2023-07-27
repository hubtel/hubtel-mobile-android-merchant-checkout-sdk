package com.hubtel.sdk.checkout.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AnalyticsConstants
import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.core_analytics.constants.UIType
import com.hubtel.core_utils.constants.AppSection
import com.hubtel.sdk.checkout.platform.analytics.events.AnalyticsEvent
import com.hubtel.sdk.checkout.platform.analytics.events.EventParams

internal class TapEvent(
    private val tapId: String,
    private val tapName: String,
    private val tapShortName: String,
    private val uiType: UIType,
    private val section: AppSection,
) : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.Tap.rawValue


    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.Tap.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.TAP_ID to tapId,
                AnalyticsConstants.EventStoreParam.TAP_NAME to tapName,
                AnalyticsConstants.EventStoreParam.TAP_SHORT_NAME to tapShortName,
                AnalyticsConstants.EventStoreParam.SECTION_NAME to section.rawValue,
                AnalyticsConstants.EventStoreParam.UI_TYPE to uiType.rawValue,
            )
        )
}
