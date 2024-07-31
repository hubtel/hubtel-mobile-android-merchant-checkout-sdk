package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

/*
 TODO:   final String? id;

  ADD MISSING PROPERTIES
 */
internal data class ThreeDSSetupInfo(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("deviceDataCollectionUrl")
    val deviceDataCollectionUrl: String?,
    @SerializedName("html")
    val html: String?,
    @SerializedName("status")
    val status: String?,
)


internal data class ThreeDSEnrollResponse(
    @SerializedName("amount")
    val amount: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("charges")
    val charges: String?,

    @SerializedName("jwt")
    val jwt: String?,

    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("deviceDataCollectionUrl")
    val deviceDataCollectionUrl: String?,
    @SerializedName("html")
    val html: String?,
    @SerializedName("cardStatus")
    val cardStatus: String?,
)

//String? transactionId;
//String? description;
//String? clientReference;
//double? amount;
//double? charges;
//String? customData;
//String? jwt;
//String? html;
//String? processor;
//
//String? cardStatus;