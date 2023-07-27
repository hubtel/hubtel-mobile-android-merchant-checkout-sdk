package com.hubtel.sdk.checkout.platform.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hubtel.sdk.checkout.R

internal enum class WalletImages(
    @DrawableRes val logo: Int,
) {
    Airtel(R.drawable.checkout_logo_airtel_money),
    MTN(R.drawable.checkout_mtn_momo),
    Vodafone(R.drawable.checkout_vodafone_cash),
    Tigo(R.drawable.checkout_logo_airtel_money),
    Visa(R.drawable.checkout_visa_colored),
    Mastercard(R.drawable.checkout_mastercard_colored),
}


internal enum class WalletProvider(
    val provider: String,
    @StringRes val providerNameResId: Int,
    val walletImages: WalletImages,
) {
    MTN("mtn", R.string.checkout_mtn_mobile_money, WalletImages.MTN),
    Vodafone("vodafone", R.string.checkout_vodafone_cash, WalletImages.Vodafone),
    AirtelTigo("airtel", R.string.checkout_airtel_tigo_money, WalletImages.Airtel),
    Tigo("tigo", R.string.checkout_airtel_tigo_money, WalletImages.Airtel),
    //
    Visa("visa", R.string.checkout_visa, WalletImages.Visa),
    Mastercard("mastercard", R.string.checkout_mastercard, WalletImages.Mastercard),
}

internal fun String.toWalletProvider(): WalletProvider? {
    return WalletProvider.values().firstOrNull {
        it.provider == this
    }
}

