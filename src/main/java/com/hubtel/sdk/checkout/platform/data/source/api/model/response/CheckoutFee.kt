package com.hubtel.sdk.checkout.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

internal data class CheckoutFee(
    @SerializedName("amount")
    val feeAmount: Double?,
    @SerializedName("name")
    val feeName: String?
)