package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

data class PaymentChannelResponse (
    @SerializedName("businessId")
    val businessID: String?,

    @SerializedName("businessName")
    val businessName: String?,

    @SerializedName("businessLogoUrl")
    val businessLogoURL: String?,

    @SerializedName("channels")
    val channels: List<String>?,

    @SerializedName("requireNationalID")
    val requireNationalID: Boolean?,

    @SerializedName("isHubtelInternalMerchant")
    val isHubtelInternalMerchant: Boolean?
)