package com.hubtel.sdk.checkout.platform.data.source.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = com.hubtel.sdk.checkout.platform.data.source.db.dao.WalletDao.NAME)
data class DbWallet(
    @ColumnInfo(name = "id")
    @PrimaryKey val id: String,

    @ColumnInfo(name = "customerID")
    val customerID: String?,

    @ColumnInfo(name = "accountName")
    val accountName: String?,

    @ColumnInfo(name = "accountNo")
    val accountNumber: String?,

    @ColumnInfo(name = "type")
    val type: String?,

    @ColumnInfo(name = "providerID")
    val providerID: String?,

    @ColumnInfo(name = "provider")
    val provider: String?,

    @ColumnInfo(name = "providerType")
    val providerType: String?,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "expiry")
    val expiry: String?,

    @ColumnInfo(name = "curentBalance")
    val currentBalance: String?,

    @ColumnInfo(name = "availBalance")
    val availableBalance: String?,

    @ColumnInfo(name = "secret")
    val secret: String?,

    @ColumnInfo(name = "cvv")
    val cvv: String?,

    @ColumnInfo(name = "countryCode")
    val countryCode: String?,

    @ColumnInfo(name = "walletImageUrl")
    val walletImageUrl: String?,

    @ColumnInfo(name = "hasGateKeeperPass")
    val hasGateKeeperPass: Boolean?,

    @ColumnInfo(name = "isSelected")
    val isSelected: Boolean? = false,

    @ColumnInfo(name = "createdAt")
    val createdAt: String?,

    @ColumnInfo(name = "updateAt")
    val updateAt: String?,

    @ColumnInfo(name = "isLocalWallet")
    val isLocalWallet: Boolean? = false,
)