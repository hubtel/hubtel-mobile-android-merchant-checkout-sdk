package com.hubtel.merchant.checkout.sdk.ux.model

import android.os.Parcel
import android.os.Parcelable
import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig

internal data class CheckoutConfig(
    val apiKey: String?,
    val posSalesId: String?,
    val amount: Double,
    val msisdn: String?,
    val callbackUrl: String?,
    val clientReference: String?,
    val description: String?,
    val themeConfig: ThemeConfig?,
    val showCancelAction : Boolean
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ThemeConfig::class.java.classLoader),
        parcel.readInt() == 1
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(apiKey)
        parcel.writeString(posSalesId)
        parcel.writeDouble(amount)
        parcel.writeString(msisdn)
        parcel.writeString(callbackUrl)
        parcel.writeString(clientReference)
        parcel.writeString(description)
        parcel.writeParcelable(themeConfig, flags)
        parcel.writeInt(if (showCancelAction) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckoutConfig> {
        override fun createFromParcel(parcel: Parcel): CheckoutConfig {
            return CheckoutConfig(parcel)
        }

        override fun newArray(size: Int): Array<CheckoutConfig?> {
            return arrayOfNulls(size)
        }
    }
}