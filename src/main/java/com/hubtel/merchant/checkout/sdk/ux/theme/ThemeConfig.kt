package com.hubtel.merchant.checkout.sdk.ux.theme

import androidx.annotation.ColorInt
import java.io.Serializable

data class ThemeConfig(
    @ColorInt val primaryColor: Int
): Serializable

//data class ThemeConfig(
//    @ColorInt val primaryColor: Int
//) : Parcelable {
//    constructor(parcel: Parcel) : this(parcel.readInt()) {
//    }
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(primaryColor)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<ThemeConfig> {
//        override fun createFromParcel(parcel: Parcel): ThemeConfig {
//            return ThemeConfig(parcel)
//        }
//
//        override fun newArray(size: Int): Array<ThemeConfig?> {
//            return arrayOfNulls(size)
//        }
//    }
//}