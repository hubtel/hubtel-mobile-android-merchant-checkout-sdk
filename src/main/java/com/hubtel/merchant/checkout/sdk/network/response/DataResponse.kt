package com.hubtel.merchant.checkout.sdk.network.response

import com.google.gson.annotations.SerializedName

data class DataResponse2<T>(
    @SerializedName("data")
    val data: T?,

    @SerializedName("developer_message")
    val developerMessage: String?,

    @SerializedName("error")
    val error: Boolean = false,

    @SerializedName("message")
    val message: String?,

    @SerializedName("status", alternate = ["code"])
    val code: String?
)

