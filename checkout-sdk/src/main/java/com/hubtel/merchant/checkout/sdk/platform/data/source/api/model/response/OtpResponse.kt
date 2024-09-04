package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json        = Json { allowStructuredMapKeys = true }
// val otpResponse = json.parse(OtpResponse.serializer(), jsonString)


data class OtpResponse (
    val customerMsisdn: String?,
    val verificationType: String?,
    val preapprovalStatus: String?,

    @SerializedName("clientReferenceId")
    val clientReferenceID: String?,

    val skipOtp: Boolean?,
)


data class OtpRequestResponse(
    @SerializedName("requestId") val requestId: String?,
    @SerializedName("otpPrefix") val otpPrefix: String?,
    @SerializedName("otpApprovalStatus") val otpApprovalStatus: String?
)