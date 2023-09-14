package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

data class CheckoutInfo(
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("customData")
    val customData: String?,
    @SerializedName("jwt")
    val jwt: String?,
)


data class MomoCheckoutInfo(
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("amountAfterCharges")
    val amountAfterCharges: Double?,
    @SerializedName("amountCharged")
    val amountCharged: Double?,
    @SerializedName("deliveryFee")
    val deliveryFee: Double?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
)


/*

let transactionId: String?
      charges: Double?
      amount: Double?
      amountAfterCharges: Double?
      amountCharged: Double?
      deliveryFee: Double?
      description: String?
      clientReference: String?

 */