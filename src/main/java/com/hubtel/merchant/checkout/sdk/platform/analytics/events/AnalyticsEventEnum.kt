package com.hubtel.merchant.checkout.sdk.platform.analytics.events

import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.core_utils.constants.AppSection

/**
 * An interface implemented by AppSection enum classes to provide
 * unique event identification which have a [String] value of double-underscore(__)
 * separated values of the format {section}__{page name}__{event type}__{event value}.
 * This format allows the parts of the unique eventID to be retrieved for logging.*/
interface AnalyticsEventEnum {

    val value: String

}

private fun AnalyticsEventEnum.splitValueParts(): List<String> {
    return value.split("__")
}

internal val AnalyticsEventEnum.section: AppSection
    get() {
        val sectionValue = splitValueParts()
            .getOrNull(0)
            ?.trim()
            ?.lowercase()

        return when (sectionValue) {
            "account" -> AppSection.Account
            "airtime_data_and_bills" -> AppSection.AirtimeDataAndBills
            "checkout" -> AppSection.Checkout
            "event_tickets" -> AppSection.EventTickets
            "food" -> AppSection.Food
            "health" -> AppSection.Health
            "home" -> AppSection.Home
            "insurance" -> AppSection.Insurance
            "onboarding" -> AppSection.Onboarding
            "send_money" -> AppSection.SendMoney
            "shop" -> AppSection.Shop
            "sms_and_money" -> AppSection.SmsAndMoney
            "take_payments" -> AppSection.TakePayment
            else -> AppSection.Other
        }
    }

internal val AnalyticsEventEnum.pageName: String
    get() = splitValueParts().getOrNull(1)
        ?.trim()?.lowercase() ?: ""

internal val AnalyticsEventEnum.eventTypeString: String
    get() = splitValueParts().getOrNull(2)
        ?.trim()?.lowercase() ?: ""

internal val AnalyticsEventEnum.eventValue: String
    get() = splitValueParts().getOrNull(3)
        ?.trim()?.lowercase() ?: ""

internal val AnalyticsEventEnum.eventType: AppEventType
    get() {
        return when (eventTypeString) {
            "tap" -> AppEventType.Tap
            "view" -> AppEventType.View
            "search" -> AppEventType.Search
            "share" -> AppEventType.Share
            "report" -> AppEventType.Report
            else -> AppEventType.Other
        }
    }