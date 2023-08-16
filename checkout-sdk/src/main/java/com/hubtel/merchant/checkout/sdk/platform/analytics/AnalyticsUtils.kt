package com.hubtel.merchant.checkout.sdk.platform.analytics

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.hubtel.core_utils.constants.AppSection
import com.hubtel.core_utils.extensions.formatYyyyMMddHHmmss
import com.hubtel.merchant.checkout.sdk.BuildConfig
import com.hubtel.merchant.checkout.sdk.platform.analytics.api.EventStoreApiService
import com.hubtel.merchant.checkout.sdk.platform.analytics.constants.AnalyticsConstants
import com.hubtel.merchant.checkout.sdk.platform.analytics.constants.AnalyticsProvider
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.ApiRequestEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.BeginPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseFailedEvent
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@SuppressLint("StaticFieldLeak") // uses application context
object AnalyticsUtils {

    private lateinit var context: Context

    private var eventStoreApiService: EventStoreApiService? = null
    private var checkoutPrefManager: CheckoutPrefManager? = null

    @Volatile
    internal var appSection: AppSection? = null

    private var sessionId: String = ""

    /**
     * Initialize analytics setup for app. This should be called as early
     * as possible. Preferably in [Application.onCreate].
     * */
    fun init(context: Context) {
        AnalyticsUtils.context = context
//        eventStoreApiService = EventStoreApiService(appPrefManager?.userAuthToken)
        sessionId = UUID.randomUUID().toString()

        initEventStore()
    }

    private fun initEventStore() {
        if (!EventStoreApiService.hasToken) {
            eventStoreApiService = EventStoreApiService(BuildConfig.API_KEY)
        }
    }

    // todo document
    private fun logEvent(
        event: AnalyticsEvent,
        providers: List<AnalyticsProvider> = AnalyticsProvider.DEFAULT,
    ) {
        providers.forEach { analyticsProvider ->
            when (analyticsProvider) {
                AnalyticsProvider.EVENT_STORE -> logEventStoreEvent(event)
                else -> {}
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun logEventStoreEvent(event: AnalyticsEvent) {
        initEventStore()

        val eventStoreEventParams = event.eventStoreEventParams
        val eventStoreOutputEvent = mutableMapOf<String, Any?>()

        fun buildCustomerMap(): EventParams {
            return mapOf(
                AnalyticsConstants.EventStoreParam.CUSTOMER_PHONE_NUMBER to checkoutPrefManager?.customerPhoneNumber,
                AnalyticsConstants.EventStoreParam.SESSION_ID to sessionId,
            )
        }

        fun buildDefaultPageMap(): EventParams {
            return mapOf(
                AnalyticsConstants.EventStoreParam.OS to "Android",
                AnalyticsConstants.EventStoreParam.APP_NAME to "Hubtel",
                AnalyticsConstants.EventStoreParam.APP_VERSION to BuildConfig.SDK_VERSION,
                AnalyticsConstants.EventStoreParam.TIME to Date().formatYyyyMMddHHmmss(),
            )
        }


        eventStoreOutputEvent[AnalyticsConstants.EventStoreParam.ACTION] =
            eventStoreEventParams[AnalyticsConstants.EventStoreParam.ACTION]
        eventStoreOutputEvent[AnalyticsConstants.EventStoreParam.CUSTOMER] = buildCustomerMap()
        eventStoreOutputEvent[AnalyticsConstants.EventStoreParam.PAGE] =
            buildDefaultPageMap().toMutableMap()
                .apply {
                    val pageMap = eventStoreEventParams[AnalyticsConstants.EventStoreParam.PAGE]

                    if (pageMap is Map<*, *>) {
                        pageMap.entries.forEach { (key, value) ->
                            this += (key.toString() to value)
                        }
                    }
                }


        // add customer info


        GlobalScope.launch {
            try {
                eventStoreApiService?.postEvent(eventStoreOutputEvent)
            } catch (ex: Exception) {
                Timber.i(ex.message)
            }
        }
    }

    fun recordAnalyticsEvent(event: AnalyticsEvent) {
        logEvent(event)
    }

    internal fun recordAnalyticsEvent(event: ApiRequestEvent) {
        logEvent(
            event.copy(appSection = appSection),
            listOf(AnalyticsProvider.MIX_PANEL, AnalyticsProvider.EVENT_STORE)
        )
    }

    internal fun recordAnalyticsEvent(event: PurchaseEvent) {
        logEvent(event, AnalyticsProvider.DEFAULT + AnalyticsProvider.APPS_FLYER)
    }

    internal fun recordAnalyticsEvent(event: PurchaseFailedEvent) {
        logEvent(event)
    }

    internal fun recordAnalyticsEvent(event: BeginPurchaseEvent) {
        logEvent(event, AnalyticsProvider.DEFAULT + AnalyticsProvider.APPS_FLYER)
    }

}