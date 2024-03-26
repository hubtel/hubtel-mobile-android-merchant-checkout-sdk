package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import android.os.Parcel
import android.os.Parcelable
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
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeString(description)
        parcel.writeString(clientReference)
        parcel.writeValue(amount)
        parcel.writeValue(charges)
        parcel.writeString(customData)
        parcel.writeString(jwt)
        parcel.writeString(preapprovalStatus)
        parcel.writeString(customerMsisdn)
        parcel.writeString(verificationType)
        parcel.writeString(hubtelPreapprovalId)
        parcel.writeString(otpPrefix)
        parcel.writeValue(skipOtp)
        parcel.writeString(cardStatus)
        parcel.writeString(customerName)
        parcel.writeString(email)
        parcel.writeString(amountAfterCharges)
        parcel.writeString(amountCharged)
        parcel.writeString(deliveryFee)
        parcel.writeString(invoiceNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutInfo> {
        override fun createFromParcel(parcel: Parcel): CheckoutInfo {
            return CheckoutInfo(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutInfo?> {
            return arrayOfNulls(size)
        }
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