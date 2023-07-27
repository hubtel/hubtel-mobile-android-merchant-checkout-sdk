package com.hubtel.sdk.checkout.ux.model

import android.os.Parcel
import android.os.Parcelable
import com.hubtel.sdk.checkout.platform.data.source.api.model.response.PaymentStatus

data class CheckoutStatus(
    val transactionId: String?,
    val isCanceled: Boolean,
    val isPaymentSuccessful: Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(transactionId)
        parcel.writeByte(if (isCanceled) 1 else 0)
        parcel.writeByte(if (isPaymentSuccessful) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutStatus> {

        const val CHECKOUT_RESULT = "com.hubtel.checkout.result"

        override fun createFromParcel(parcel: Parcel): CheckoutStatus {
            return CheckoutStatus(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutStatus?> {
            return arrayOfNulls(size)
        }
    }
}