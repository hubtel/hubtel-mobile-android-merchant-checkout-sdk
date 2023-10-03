package com.hubtel.merchant.checkout.sdk.platform.model

import androidx.core.text.isDigitsOnly
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

internal data class Wallet(
    val id: Long,
    val accountNumber: String?,
    val accountName: String?,
    val expiry: String?,
    val cvv: String?,
    val provider: String?
)

data class Wallet2(
    @SerializedName("id")
    @PrimaryKey val id: String,

    @SerializedName("customerID")
    val customerID: String?,

    @SerializedName("accountName")
    val accountName: String?,

    @SerializedName("accountNo")
    val accountNumber: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("providerID")
    val providerID: String?,

    @SerializedName("provider")
    val provider: String?,

    @SerializedName("providerType")
    val providerType: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("expiry")
    val expiry: String?,

    @SerializedName("curentBalance")
    val currentBalance: Double?,

    @SerializedName("availBalance")
    val availableBalance: Double?,

    @SerializedName("secret")
    val secret: String?,

    @SerializedName("countryCode")
    val countryCode: String?,

    @SerializedName("walletImageUrl")
    val walletImageUrl: String?,

    @SerializedName("hasGateKeeperPass")
    val hasGateKeeperPass: Boolean?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updateAt")
    val updateAt: String?,

    @SerializedName("isLocalWallet")
    val isLocalWallet: Boolean? = false,
) {

    val walletType: WalletType
        get() {
            return WalletType.values().find {
                it.optionValue == type?.lowercase()
            } ?: WalletType.Hubtel
        }

    val isWalletComplete: Boolean
        get() {
            return secret?.isDigitsOnly() == true
                    && accountNumber?.isDigitsOnly() == true
        }

    val middle: String?
        get() {
            return if (accountNumber?.length == 16) {
                accountNumber.substring(6..11)
            } else null
        }
}

enum class WalletType(val optionValue: String) {
    Card("card"),
    Momo("momo"),
    Gratis("hubtel"),
    Hubtel("hubtel"),
}

internal data class OtherPaymentWallet(val id: Long, val number: String?)