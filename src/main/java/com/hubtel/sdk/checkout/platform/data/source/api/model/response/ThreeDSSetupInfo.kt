package com.hubtel.sdk.checkout.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

internal data class ThreeDSSetupInfo(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("deviceDataCollectionUrl")
    val deviceDataCollectionUrl: String?
)