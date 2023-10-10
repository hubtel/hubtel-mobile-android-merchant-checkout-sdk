package com.hubtel.merchant.checkout.sdk.platform.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hubtel.merchant.checkout.sdk.R


enum class WalletImages(
    @DrawableRes val logo: Int,
) {
    Airtel(R.drawable.checkout_logo_airtel_money),
    MTN(R.drawable.checkout_mtn_momo),
    Vodafone(R.drawable.checkout_vodafone_cash),
    Tigo(R.drawable.checkout_logo_airtel_money),
    Visa(R.drawable.checkout_visa_colored),
    Mastercard(R.drawable.checkout_mastercard_colored),
    Hubtel(R.drawable.checkout_ic_hubtel),
    ZeePay(R.drawable.checkout_ic_zeepay),
    GMoney(R.drawable.checkout_ic_gmoney)
}


enum class WalletProvider(
    val provider: String,
    @StringRes val providerNameResId: Int,
    val walletImages: WalletImages,
    @StringRes val description: Int? = null
) {
    MTN("mtn", R.string.checkout_mtn_mobile_money, WalletImages.MTN),
    Vodafone(
        "vodafone",
        R.string.checkout_vodafone_cash,
        WalletImages.Vodafone
    ),
    AirtelTigo("airtel", R.string.checkout_airtel_tigo_money, WalletImages.Airtel),
    Tigo(
        "tigo",
        R.string.checkout_airtel_tigo_money,
        WalletImages.Airtel
    ),

    //
    Visa("visa", R.string.checkout_visa, WalletImages.Visa),
    Mastercard(
        "mastercard", R.string.checkout_mastercard, WalletImages.Mastercard
    ),

    //
    Hubtel(
        "hubtel-gh",
        R.string.checkout_hubtel,
        WalletImages.Hubtel,
        R.string.checkout_hubtel_balance_debit_msg
    ),
    GMoney("g-money", R.string.checkout_g_money, WalletImages.GMoney),
    ZeePay("zeepay", R.string.checkout_zeepay, WalletImages.ZeePay, R.string.checkout_zeepay_steps),
    BankCard("", R.string.checkout_bank_card, WalletImages.Visa)
}

internal fun String.toWalletProvider(): WalletProvider? {
    return WalletProvider.values().firstOrNull {
        it.provider == this
    }
}

internal enum class OtherPaymentProvider(
    val provider: String
) {
    Hubtel("hubtel"),
    GMoney("gmoney"),
    ZeePay("zeepay")
}
