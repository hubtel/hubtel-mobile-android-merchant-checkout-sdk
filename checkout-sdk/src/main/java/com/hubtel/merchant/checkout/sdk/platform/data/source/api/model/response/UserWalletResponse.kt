package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

// To parse the JSON, install kotlin's serialization plugin and do:
//
// val json       = Json { allowStructuredMapKeys = true }
// val userWallet = json.parse(UserWallet.serializer(), jsonString)

data class UserWalletResponse (
    val id: Long,

    @SerializedName("clientId")
    val clientID: Long,

    @SerializedName("accountingId")
    val accountingID: String? = null,

    @SerializedName("savingsAccountingId")
    val savingsAccountingID: String? = null,

    @SerializedName("externalId")
    val externalID: String? = null,

    @SerializedName("customerId")
    val customerID: Long,

    val accountNo: String? = null,
    val accountName: String? = null,

    @SerializedName("providerId")
    val providerID: String? = null,

    val provider: String? = null,
    val providerType: String? = null,
    @SerializedName("curentBalance")
    val currentBalance: Long,
    val availBalance: Long,
    val type: String? = null,
    val countryCode: String? = null,
    val status: String? = null,
    val expiry: String? = null,
    val secret: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val street: String? = null,
    val city: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val email: String? = null,
    val currency: String? = null,
    val cardNumber: String? = null,
    val expiryMonth: String? = null,
    val expiryYear: String? = null,
    val token: String? = null,
    val note: String? = null,

    @SerializedName("walletImageUrl")
    val walletImageURL: String? = null,

    val isTokenized: Boolean,
    val retryCount: Long,
    val hasGateKeeperPass: String? = null,
    val gateKeeperPassNote: String? = null,
    val idCardFrontSide: String? = null,
    val idCardBackSide: String? = null,
    val idCardHolderSelfie: String? = null,
    val createdAt: String? = null,
    val updateAt: String? = null
)
