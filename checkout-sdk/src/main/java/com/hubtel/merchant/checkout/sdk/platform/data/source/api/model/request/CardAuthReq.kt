package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request

import com.google.gson.annotations.SerializedName

data class CardAuthRequest(
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("cardHolderName")
    val cardHolderName: String,
    @SerializedName("cardNumber")
    val cardNumber: String,
    @SerializedName("cvv")
    val cvv: String,
    @SerializedName("expiryMonth")
    val expiryMonth: String,
    @SerializedName("expiryYear")
    val expiryYear: String,
    @SerializedName("customerMsisdn")
    val customerMsisdn: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("clientReference")
    val clientReference: String,
    @SerializedName("callbackUrl")
    val callbackUrl: String,
    @SerializedName("integrationChannel")
    val integrationChannel: String = "UnifiedCheckout-Kotlin"
)