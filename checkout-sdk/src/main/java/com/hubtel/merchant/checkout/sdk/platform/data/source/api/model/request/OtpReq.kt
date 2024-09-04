package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request

import com.google.gson.annotations.SerializedName

internal data class OtpReq(
    @SerializedName("customerMsisdn")
    val customerMsisdn: String,
    @SerializedName("hubtelPreApprovalId")
    val hubtelPreApprovalID: String,
    @SerializedName("clientReferenceId")
    val clientReferenceID: String,
    @SerializedName("OtpCode")
    val otpCode: String
)

internal data class PaymentOtpReq(
    @SerializedName("msisdn")
    val customerMsisdn: String,
    @SerializedName("requestId")
    val requestId: String,
    @SerializedName("OtpCode")
    val otpCode: String
)

internal data class GetOtpReq(
    @SerializedName("customerMsisdn")
    val customerMsisdn: String,
)