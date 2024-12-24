package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CheckoutInfo(
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReferenceId")
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

    @SerializedName("cardStatus")
    val cardStatus: String? ,

    @SerializedName("customerName")
    val customerName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("amountAfterCharges")
    val amountAfterCharges: String?,

    @SerializedName("amountCharged")
    val amountCharged: String?,

    @SerializedName("deliveryFee")
    val deliveryFee: String?,

    @SerializedName("invoiceNumber")
    val invoiceNumber: String?
): Serializable {
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

    val getBankCardStatus: BankCardStatus
        get() = when (cardStatus?.lowercase()) {
            BankCardStatus.AUTHENTICATION_SUCCESSFUL.rawValue -> BankCardStatus.AUTHENTICATION_SUCCESSFUL
            BankCardStatus.PENDING_AUTHENTICATION.rawValue -> BankCardStatus.PENDING_AUTHENTICATION
            else -> BankCardStatus.OTHERS
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

enum class BankCardStatus(val rawValue: String) {
    AUTHENTICATION_SUCCESSFUL("authentication_successful"),
    PENDING_AUTHENTICATION("pending_authentication"),
    OTHERS("others")
}