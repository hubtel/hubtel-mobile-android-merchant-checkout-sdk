package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName


internal data class TransactionStatusInfo(
    @SerializedName("amountAfterFees")
    val amountAfterFees: Double?,
    @SerializedName("checkoutId")
    val checkoutId: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("countryCode")
    val countryCode: String?,
    @SerializedName("currencyCode")
    val currencyCode: String?,
    @SerializedName("disputed")
    val disputed: Boolean?,
    @SerializedName("disputedAmount")
    val disputedAmount: Double?,
    @SerializedName("disputedAmountFee")
    val disputedAmountFee: Double?,
    @SerializedName("fee")
    val fee: Double?,
    @SerializedName("invoiceStatus")
    val invoiceStatus: String?,
    @SerializedName("invoiceToken")
    val invoiceToken: String?,
    @SerializedName("mobileNumber")
    val mobileNumber: String?,
    @SerializedName("paymentMethod")
    val paymentMethod: String?,
    @SerializedName("startDate")
    val startDate: String?,
    @SerializedName("totalAmountRefunded")
    val totalAmountRefunded: Int?,
    @SerializedName("transactionAmount")
    val transactionAmount: Double?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("transactionStatus")
    val transactionStatus: String?,
    @SerializedName("transactionType")
    val transactionType: String?
) {

    val paymentStatus: PaymentStatus
        get() {
            return when (transactionStatus?.lowercase()) {
                "success" -> PaymentStatus.PAID
                "failed" -> PaymentStatus.UNPAID
                else -> PaymentStatus.PENDING
            }
        }
}

internal enum class PaymentStatus {
    UNPAID,
    PAID,
    PENDING,
}