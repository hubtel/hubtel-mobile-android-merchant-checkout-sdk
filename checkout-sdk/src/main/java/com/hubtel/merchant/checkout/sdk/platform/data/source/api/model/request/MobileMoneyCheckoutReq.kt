package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request
import com.google.gson.annotations.SerializedName


data class MobileMoneyCheckoutReq(
    @SerializedName("Amount")
    val amount: Double?,
    @SerializedName("Channel")
    val channel: String?,
    @SerializedName("ClientReference")
    val clientReference: String?,
    @SerializedName("CustomerMsisdn")
    val customerMsisdn: String?,
    @SerializedName("CustomerName")
    val customerName: String?,
    @SerializedName("Description")
    val description: String?,
    @SerializedName("PrimaryCallbackUrl")
    val primaryCallbackUrl: String?
)