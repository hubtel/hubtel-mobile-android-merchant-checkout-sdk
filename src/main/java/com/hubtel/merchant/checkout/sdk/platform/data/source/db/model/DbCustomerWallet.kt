package com.hubtel.merchant.checkout.sdk.platform.data.source.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.dao.HubtelWalletDao

@Entity(tableName = HubtelWalletDao.NAME)
data class HubtelWallet(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "clientID")
    val clientID: Long?,

    @ColumnInfo(name = "accountingID")
    val accountingID: String?,

    @ColumnInfo(name = "savingsAccountingID")
    val savingsAccountingID: String?,

    @ColumnInfo(name = "externalID")
    val externalID: String?,

    @ColumnInfo(name = "customerID")
    val customerID: Long?,

    @ColumnInfo(name = "accountNo")
    val accountNo: String?,

    @ColumnInfo(name = "accountName")
    val accountName: String?,

    @ColumnInfo(name = "providerID")
    val providerID: String?,

    @ColumnInfo(name = "provider")
    val provider: String?,

    @ColumnInfo(name = "providerType")
    val providerType: String?,

    @ColumnInfo(name = "curentBalance")
    val currentBalance: Double?,

    @ColumnInfo(name = "availBalance")
    val availBalance: Double?,

    @ColumnInfo(name = "type")
    val type: String?,

    @ColumnInfo(name = "countryCode")
    val countryCode: String?,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "expiry")
    val expiry: String?,

    @ColumnInfo(name = "secret")
    val secret: String?,

    @ColumnInfo(name = "firstName")
    val firstName: String?,

    @ColumnInfo(name = "lastName")
    val lastName: String?,


    @ColumnInfo(name = "street")
    val street: String?,


    @ColumnInfo(name = "city")
    val city: String?,

    @ColumnInfo(name = "postalCode")
    val postalCode: String?,

    @ColumnInfo(name = "country")
    val country: String?,

    @ColumnInfo(name = "email")
    val email: String?,

    @ColumnInfo(name = "currency")
    val currency: String?,

    @ColumnInfo(name = "cardNumber")
    val cardNumber: String?,

    @ColumnInfo(name = "expiryMonth")
    val expiryMonth: String?,

    @ColumnInfo(name = "expiryYear")
    val expiryYear: String?,

    @ColumnInfo(name = "token")
    val token: String?,

    @ColumnInfo(name = "note")
    val note: String?,

    @ColumnInfo(name = "walletImageURL")
    val walletImageURL: String?,

    @ColumnInfo(name = "isTokenized")
    val isTokenized: Boolean?,

    @ColumnInfo(name = "retryCount")
    val retryCount: Long?,

    @ColumnInfo(name = "hasGateKeeperPass")
    val hasGateKeeperPass: Boolean?,

    @ColumnInfo(name = "gateKeeperPassNote")
    val gateKeeperPassNote: String?,

    @ColumnInfo(name = "idCardFrontSide")
    val idCardFrontSide: String?,

    @ColumnInfo(name = "idCardBackSide")
    val idCardBackSide: String?,

    @ColumnInfo(name = "idCardHolderSelfie")
    val idCardHolderSelfie: String?,

    @ColumnInfo(name = "createdAt")
    val createdAt: String?,

    @ColumnInfo(name = "updateAt")
    val updateAt: String?,
)

fun HubtelWallet.toWalletResponse(): WalletResponse {
    return WalletResponse(
        id = 0L,
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