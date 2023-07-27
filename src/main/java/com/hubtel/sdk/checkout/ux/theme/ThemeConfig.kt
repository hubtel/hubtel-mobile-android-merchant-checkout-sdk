package com.hubtel.sdk.checkout.ux.theme

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.ColorInt

data class ThemeConfig(
    @ColorInt val primaryColor: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(primaryColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThemeConfig> {
        override fun createFromParcel(parcel: Parcel): ThemeConfig {
            return ThemeConfig(parcel)
        }

        override fun newArray(size: Int): Array<ThemeConfig?> {
            return arrayOfNulls(size)
        }
    }
}