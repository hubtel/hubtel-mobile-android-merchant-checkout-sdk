package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request
import com.google.gson.annotations.SerializedName


data class GetFeesReq(
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("channel")
    val channel: String?
)