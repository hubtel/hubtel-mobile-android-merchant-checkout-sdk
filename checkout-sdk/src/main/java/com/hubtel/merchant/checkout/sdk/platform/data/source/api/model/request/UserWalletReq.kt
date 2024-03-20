package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request

import com.google.gson.annotations.SerializedName

data class UserWalletReq(
    @SerializedName("accountNo")
    val accountNo: String?,
    @SerializedName("provider")
    val provider: String?,
    @SerializedName("CustomerMobileNumber")
    val customerMobileNumber: String
)