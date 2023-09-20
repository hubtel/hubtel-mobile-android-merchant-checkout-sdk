package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

data class CheckoutInfo(
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("customData")
    val customData: String?,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("preapprovalStatus")
    val preapprovalStatus: String?,

    // added
    @SerializedName("customerMsisdn")
    val customerMsisdn: String?,
    @SerializedName("verificationType")
    val verificationType: String?,
    @SerializedName("hubtelPreapprovalId")
    val hubtelPreapprovalId: String?,
    @SerializedName("otpPrefix")
    val otpPrefix: String?,
    @SerializedName("skipOtp")
    val skipOtp: Boolean?,
) {
    val getVerificationType: VerificationType
        get() = when(verificationType?.lowercase()) {
            VerificationType.OTP.rawValue -> VerificationType.OTP
            VerificationType.USSD.rawValue -> VerificationType.USSD
            else -> VerificationType.USSD
        }

    val getPreapprovalStatus: PreapprovalStatus
        get() = when(preapprovalStatus?.lowercase()) {
            PreapprovalStatus.APPROVED.rawValue -> PreapprovalStatus.APPROVED
            PreapprovalStatus.PENDING.rawValue -> PreapprovalStatus.PENDING
            else -> PreapprovalStatus.PENDING
        }
}

enum class VerificationType(val rawValue: String) {
    OTP("otp"),
    USSD("ussd")
}

enum class PreapprovalStatus(val rawValue: String) {
    APPROVED("approved"),
    PENDING("pending"),
}

data class CheckoutInfo2(
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("customData")
    val customData: String?,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("preapprovalStatus")

    // added
    val preapprovalStatus: String,
    @SerializedName("customerMsisdn")
    val customerMsisdn: String,
    @SerializedName("verificationType")
    val verificationType: String,
    @SerializedName("hubtelPreapprovalId")
    val hubtelPreapprovalId: String,
    @SerializedName("otpPrefix")
    val otpPrefix: String,
    @SerializedName("skipOtp")
    val skipOtp: Boolean,
)

/*

    {
        "customerMsisdn": "233551520348",
        "verificationType": "OTP",
        "preapprovalStatus": "PENDING",
        "hubtelPreapprovalId": "9938799b168d4f5bbe9653b31316baaa",
        "clientReferenceId": "test1",
        "otpPrefix": "PFEW",
        "skipOtp": false
    }

    let channel: String?
    let customerMsisdn: String?
    let primaryCallbackUrl: String?
    let clientReference: String?
    let amount: String?
    let description: String?
    let skipOtp: Bool?
    let otpPrefix: String?

 */


data class MomoCheckoutInfo(
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("amountAfterCharges")
    val amountAfterCharges: Double?,
    @SerializedName("amountCharged")
    val amountCharged: Double?,
    @SerializedName("deliveryFee")
    val deliveryFee: Double?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
)


/*

let transactionId: String?
      charges: Double?
      amount: Double?
      amountAfterCharges: Double?
      amountCharged: Double?
      deliveryFee: Double?
      description: String?
      clientReference: String?

 */