package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request

import com.google.gson.annotations.SerializedName


data class ThreeDSSetupReq(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("callbackUrl")
    val callbackUrl: String?,
    @SerializedName("cardHolderName")
    val cardHolderName: String?,
    @SerializedName("cardNumber")
    val cardNumber: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("customerMsisdn")
    val customerMsisdn: String?,
    @SerializedName("cvv")
    val cvv: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("expiryMonth")
    val expiryMonth: String?,
    @SerializedName("expiryYear")
    val expiryYear: String?,
    @SerializedName("country")

    // added
    val country: String? = "gh",
    @SerializedName("currency")
    val currency: String? = "ghs"
)
