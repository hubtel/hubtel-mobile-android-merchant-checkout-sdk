package com.hubtel.merchant.checkout.sdk.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AnalyticsConstants
import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams
import com.hubtel.core_utils.constants.AppSection

data class ApiRequestEvent(
    private val url: String,
    private val statusCode: Int,
    private val status: Status,
    private val errorMessage: String,
    private val responseTimeInSeconds: Double,
    private val responseSizeInKB: Double,
    private val responseSizeInMB: Double,
    private val appSection: AppSection? = null
) : AnalyticsEvent() {

    override val firebaseEventName: String
        get() = ""

    override val firebaseEventParams: EventParams
        get() = mapOf()

    override val mixpanelEventName: String
        get() = AppEventType.ApiRequest.rawValue

    override val mixPanelEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.Param.API_BASE_URL to baseUrl,
            AnalyticsConstants.Param.API_URL to url,
            AnalyticsConstants.Param.API_STATUS to status.rawValue,
            AnalyticsConstants.Param.API_STATUS_CODE to statusCode,
            AnalyticsConstants.Param.API_ERROR_MESSAGE to errorMessage,
            AnalyticsConstants.Param.API_RESPONSE_TIME_IN_SECONDS to responseTimeInSeconds,
            AnalyticsConstants.Param.API_RESPONSE_SIZE_IN_KILOBYTES to responseSizeInKB,
            AnalyticsConstants.Param.API_RESPONSE_SIZE_IN_MEGA_BYTES to responseSizeInMB,
            AnalyticsConstants.Param.SECTION_NAME to appSection?.rawValue
        )

    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.ApiRequest.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf(
                AnalyticsConstants.EventStoreParam.API_BASE_URL to baseUrl,
                AnalyticsConstants.EventStoreParam.API_URL to url,
                AnalyticsConstants.EventStoreParam.API_STATUS to status.rawValue,
                AnalyticsConstants.EventStoreParam.API_STATUS_CODE to statusCode,
                AnalyticsConstants.EventStoreParam.API_ERROR_MESSAGE to errorMessage,
                AnalyticsConstants.EventStoreParam.API_RESPONSE_TIME_IN_SECONDS to responseTimeInSeconds,
                AnalyticsConstants.EventStoreParam.API_RESPONSE_SIZE_IN_KILOBYTES to responseSizeInKB,
                AnalyticsConstants.EventStoreParam.API_RESPONSE_SIZE_IN_MEGA_BYTES to responseSizeInMB,
                AnalyticsConstants.EventStoreParam.SECTION_NAME to appSection?.rawValue
            )
        )

    private val baseUrl: String
        get() {
            val baseUrlRegex = "^.+?[^\\\\/:](?=[?\\\\/]|\$)".toRegex()
            return baseUrlRegex.findAll(url).firstOrNull()?.value?.lowercase() ?: ""
        }

    override fun toString(): String {
        return """
            ApiRequestEvent(
                url = $url,
                statusCode = $statusCode,
                status = $status,
                responseTimeInSeconds = $responseTimeInSeconds,
                errorMessage = $errorMessage
            )
        """.trimIndent()
    }
}


enum class Status(internal val rawValue: String) {
    Success("Success"),
    Error("Error"),
    Failed("Failed"),
    Timeout("Timeout"),
    NoInternet("No Internet"),
}