package com.hubtel.sdk.checkout.ux.model

import android.os.Parcel
import android.os.Parcelable
import com.hubtel.sdk.checkout.ux.theme.ThemeConfig

internal data class CheckoutConfig(
    val apiKey: String?,
    val posSalesId: String?,
    val amount: Double,
    val msisdn: String?,
    val callbackUrl: String?,
    val clientReference: String?,
    val description: String?,
    val themeConfig: ThemeConfig?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ThemeConfig::class.java.classLoader)
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