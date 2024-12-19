package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

internal data class CheckoutFee(
    @SerializedName("fees")
    val fees: Double,
    @SerializedName("amountPayable")
    val amountPayable: Double,
    @SerializedName("checkoutType")
    val checkoutType: String,
    @SerializedName("amount")
    val amount: Double
) {
    val getCheckoutType: CheckoutType
        get() = when (checkoutType.lowercase()) {
            CheckoutType.RECEIVE_MONEY_PROMPT.rawValue -> CheckoutType.RECEIVE_MONEY_PROMPT
            CheckoutType.DIRECT_DEBIT.rawValue -> CheckoutType.DIRECT_DEBIT
            CheckoutType.PRE_APPROVAL_CONFIRM.rawValue -> CheckoutType.PRE_APPROVAL_CONFIRM
            else -> CheckoutType.RECEIVE_MONEY_PROMPT
        }
}


enum class CheckoutType(val rawValue: String) : Parcelable {
    RECEIVE_MONEY_PROMPT("receivemoneyprompt"),
    DIRECT_DEBIT("directdebit"),
    PRE_APPROVAL_CONFIRM("preapprovalconfirm");

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(rawValue)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CheckoutType> {
        override fun createFromParcel(parcel: Parcel): CheckoutType {
            val rawValue = parcel.readString()
            return CheckoutType.entries.first { it.rawValue == rawValue }
        }

        override fun newArray(size: Int): Array<CheckoutType?> = arrayOfNulls(size)
    }
}

internal data class BusinessInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("contact")
    val logo: String?
)
