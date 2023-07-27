package com.hubtel.sdk.checkout.platform.analytics.events

/**
 * A typealias to represent analytic event parameters (key - value pairs).
 * */
internal typealias EventParams = Map<String, Any?>


/**
 *  An abstract class that represent a log-able analytics event.
 * This class is meant to be extended for each unique that event
 * that is logged to the app analytics backend service/provider.
 * providers if no values are passed by the subclass of the event.
 * */
abstract class AnalyticsEvent {

    internal open val firebaseEventName: String
        get() = ""

    internal open val firebaseEventParams: EventParams
        get() = mapOf()

    internal open val mixpanelEventName: String
        get() = firebaseEventName

    internal open val mixPanelEventParams: EventParams
        get() = firebaseEventParams

    internal open val eventStoreName: String
        get() = mixpanelEventName

    internal open val eventStoreEventParams: EventParams
        get() = mapOf()


    internal open val appsFlyerEventName: String
        get() = mixpanelEventName

    internal open val appsFlyerEventParams: EventParams?
        get() = mapOf()

}

