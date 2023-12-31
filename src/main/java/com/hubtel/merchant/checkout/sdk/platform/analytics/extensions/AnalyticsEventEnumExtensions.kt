package com.hubtel.merchant.checkout.sdk.platform.analytics.extensions

import com.hubtel.merchant.checkout.sdk.platform.analytics.constants.AppEventType
import com.hubtel.merchant.checkout.sdk.platform.analytics.constants.UIType
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEventEnum
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.eventType
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.eventValue
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.pageName
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.section
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.TapEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.ViewEvent

private val AnalyticsEventEnum.uiType: UIType
    get() = when (this.eventType) {
        AppEventType.View -> {
            when {
                value.contains("__page_") -> UIType.Page
                value.contains("__dialog_") -> UIType.Dialog
                value.contains("__bottom_sheet_") -> UIType.BottomSheet
                else -> UIType.Other
            }
        }

        AppEventType.Tap -> {
            when {
                value.contains("__button_") -> UIType.Page
                value.contains("__bottom_navigation_menu_item_") -> UIType.BottomNavigationMenuItem
                else -> UIType.Other
            }
        }

        else -> UIType.Other
    }


fun AnalyticsEventEnum.toEvent(): AnalyticsEvent {
    return when (this.eventType) {
        AppEventType.View -> {
            ViewEvent(
                pageName = this.pageName,
                section = this.section,
                uiType = this.uiType,
                viewId = this.value,
                viewName = this.value.toFriendlyName(),
                viewShortName = this.eventValue.toFriendlyName(),
            )
        }

        AppEventType.Tap -> {
            return TapEvent(
                tapId = this.value,
                tapName = this.value.toFriendlyName(),
                tapShortName = this.eventValue.toFriendlyName(),
                section = this.section,
                uiType = this.uiType,
            )
        }

        else -> com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.EmptyEvent
    }
}



