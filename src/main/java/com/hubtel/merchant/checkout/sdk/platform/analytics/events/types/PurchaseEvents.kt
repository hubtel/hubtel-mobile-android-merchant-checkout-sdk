package com.hubtel.merchant.checkout.sdk.platform.analytics.events.types

import com.hubtel.core_analytics.constants.AnalyticsConstants
import com.hubtel.core_analytics.constants.AppEventType
import com.hubtel.core_analytics.extensions.toFriendlyName
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.AnalyticsEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.EventParams

class PurchaseEvent(
    private val orderId: String?,
    internal val amount: Double,
    private val paymentType: String?,
    private val paymentChannel: String?,
    private val errorMessage: String? = null,
    private val purchaseOrderItems: List<PurchaseOrderItem>?,
    private val purchaseOrderItemNames: List<String>?,
    private val purchaseOrderProviders: List<String>?,
) : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.Purchase.rawValue


    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.Purchase.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.SECTION_NAME to SECTION_NAME,
                AnalyticsConstants.EventStoreParam.PURCHASE_AMOUNT to amount,
                AnalyticsConstants.EventStoreParam.PURCHASE_PAYMENT_CHANNEL to paymentChannel,
                AnalyticsConstants.EventStoreParam.PURCHASE_PAYMENT_TYPE to paymentType?.toFriendlyName(),
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEMS to purchaseOrderItems?.map {
                    mapOf(
                        AnalyticsConstants.EventStoreParam.ITEM_NAME to it.name,
                        AnalyticsConstants.EventStoreParam.QUANTITY to it.quantity,
                        AnalyticsConstants.EventStoreParam.AMOUNT to it.amount,
                        AnalyticsConstants.EventStoreParam.SECTION to SECTION_NAME,
                        AnalyticsConstants.EventStoreParam.PROVIDER to it.provider,
                    )
                },
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEM_NAMES to purchaseOrderItemNames,
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_PROVIDERS to purchaseOrderProviders
            )
        )
}

class PurchaseFailedEvent(
    internal val amount: Double,
    private val errorMessage: String?,
    private val paymentChannel: String?,
    private val paymentType: String?,
    private val purchaseOrderItems: List<PurchaseOrderItem>?,
    private val purchaseOrderItemNames: List<String>?,
    private val purchaseOrderProviders: List<String>?,
) : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.PurchaseFailed.rawValue

    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.PurchaseFailed.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf<String, Any?>(
                AnalyticsConstants.EventStoreParam.SECTION_NAME to SECTION_NAME,
                AnalyticsConstants.EventStoreParam.PURCHASE_AMOUNT to amount,
                AnalyticsConstants.EventStoreParam.PURCHASE_PAYMENT_CHANNEL to paymentChannel,
                AnalyticsConstants.EventStoreParam.PURCHASE_PAYMENT_TYPE to paymentType?.toFriendlyName(),
                AnalyticsConstants.EventStoreParam.PURCHASE_ERROR_MESSAGE to errorMessage,
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEMS to purchaseOrderItems?.map {
                    mapOf(
                        AnalyticsConstants.EventStoreParam.ITEM_NAME to it.name,
                        AnalyticsConstants.EventStoreParam.QUANTITY to it.quantity,
                        AnalyticsConstants.EventStoreParam.AMOUNT to it.amount,
                        AnalyticsConstants.EventStoreParam.SECTION to SECTION_NAME,
                        AnalyticsConstants.EventStoreParam.PROVIDER to it.provider,
                    )
                },
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEM_NAMES to purchaseOrderItemNames,
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_PROVIDERS to purchaseOrderProviders
            )
        )
}

class BeginPurchaseEvent(
    internal val amount: Double,
    private val purchaseOrderItems: List<PurchaseOrderItem>?,
    private val purchaseOrderItemNames: List<String>?,
    private val purchaseOrderProviders: List<String>?,
) : AnalyticsEvent() {

    override val mixpanelEventName: String
        get() = AppEventType.BeginPurchase.rawValue

    override val eventStoreEventParams: EventParams
        get() = mapOf(
            AnalyticsConstants.EventStoreParam.ACTION to mapOf(
                AnalyticsConstants.EventStoreParam.ACTION_NAME to AppEventType.Purchase.rawValue,
            ),
            AnalyticsConstants.EventStoreParam.PAGE to mapOf(
                AnalyticsConstants.EventStoreParam.SECTION_NAME to SECTION_NAME,
                AnalyticsConstants.EventStoreParam.PURCHASE_AMOUNT to amount,
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEMS to purchaseOrderItems?.map {
                    mapOf(
                        AnalyticsConstants.EventStoreParam.ITEM_NAME to it.name,
                        AnalyticsConstants.EventStoreParam.QUANTITY to it.quantity,
                        AnalyticsConstants.EventStoreParam.AMOUNT to it.amount,
                        AnalyticsConstants.EventStoreParam.SECTION to SECTION_NAME,
                        AnalyticsConstants.EventStoreParam.PROVIDER to it.provider,
                    )
                },
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_ITEM_NAMES to purchaseOrderItemNames,
                AnalyticsConstants.EventStoreParam.PURCHASE_ORDER_PROVIDERS to purchaseOrderProviders
            )
        )

}

data class PurchaseOrderItem(
    val itemId: String?,
    val name: String?,
    val quantity: Int?,
    val amount: Double?,
    val provider: String?,
)

const val SECTION_NAME = "Checkout"