package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.HubtelWallet
import java.util.Locale

data class WalletResponse(
    val id: Long? = null,

    @SerializedName("clientId")
    val clientID: Long? = null,

    @SerializedName("accountingId")
    val accountingID: String? = null,

    @SerializedName("savingsAccountingId")
    val savingsAccountingID: String? = null,

    @SerializedName("externalId")
    val externalID: String? = null,

    @SerializedName("customerId")
    val customerID: Long? = null,

    val accountNo: String? = null,
    val accountName: String? = null,

    @SerializedName("providerId")
    val providerID: String? = null,

    val provider: String? = null,
    val providerType: String? = null,

    @SerializedName("curentBalance")
    val currentBalance: Double? = null,
    val availBalance: Double? = null,
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

    val isTokenized: Boolean? = null,
    val retryCount: Long? = null,
    val hasGateKeeperPass: Boolean? = null,
    val gateKeeperPassNote: String? = null,
    val idCardFrontSide: String? = null,
    val idCardBackSide: String? = null,
    val idCardHolderSelfie: String? = null,
    val createdAt: String? = null,
    val updateAt: String? = null
) {
    val getProvider: String?
        get() {
            return when (provider?.lowercase(Locale.ROOT)) {
                "mtn" -> "MTN Mobile Money"
                "vodafone" -> "Vodafone Cash"
                "airtel tigo" -> "Airtel Tigo"
                else -> null
            }
        }

    fun toHubtelWallet(): HubtelWallet {
        return HubtelWallet(
            id = id ?: 0L,
            clientID = clientID,
            accountingID = accountingID,
            savingsAccountingID = savingsAccountingID,
            externalID = externalID,
            customerID = customerID,
            accountNo = accountNo,
            accountName = accountName,
            providerID = providerID,
            provider = provider,
            providerType = providerType,
            currentBalance = currentBalance,
            availBalance = availBalance,
            type = type,
            countryCode = countryCode,
            status = status,
            expiry = expiry,
            secret = secret,
            firstName = firstName,
            lastName = lastName,
            street = street,
            city = city,
            postalCode = postalCode,
            country = country,
            email = email,
            currency = currency,
            cardNumber = cardNumber,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            token = token,
            note = note,
            walletImageURL = walletImageURL,
            isTokenized = isTokenized,
            retryCount = retryCount,
            hasGateKeeperPass = hasGateKeeperPass,
            gateKeeperPassNote = gateKeeperPassNote,
            idCardFrontSide = idCardFrontSide,
            idCardBackSide = idCardBackSide,
            idCardHolderSelfie = idCardHolderSelfie,
            createdAt = createdAt,
            updateAt = updateAt,
        )
    }
}

data class WalletResponse2(
    val id: Long? = null,

    @SerializedName("clientId")
    val clientID: Long? = null,

    @SerializedName("accountingId")
    val accountingID: String? = null,

    @SerializedName("savingsAccountingId")
    val savingsAccountingID: String? = null,

    @SerializedName("externalId")
    val externalID: String? = null,

    @SerializedName("customerId")
    val customerID: Long? = null,

    val accountNo: String? = null,
    val accountName: String? = null,

    @SerializedName("providerId")
    val providerID: String? = null,

    val provider: String? = null,
    val providerType: String? = null,
    val curentBalance: Double? = null,
    val availBalance: Double? = null,
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

    val isTokenized: Boolean? = null,
    val retryCount: Long? = null,
    val hasGateKeeperPass: Boolean? = null,
    val gateKeeperPassNote: String? = null,
    val idCardFrontSide: String? = null,
    val idCardBackSide: String? = null,
    val idCardHolderSelfie: String? = null,
    val createdAt: String? = null,
    val updateAt: String? = null
)