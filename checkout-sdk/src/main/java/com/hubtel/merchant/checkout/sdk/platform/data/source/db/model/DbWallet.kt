package com.hubtel.merchant.checkout.sdk.platform.data.source.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.dao.WalletDao

@Entity(tableName = WalletDao.NAME)
data class DbWallet(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true) val id: Long,

    @ColumnInfo(name = "accountName")
    val accountName: String?,

    @ColumnInfo(name = "accountNo")
    val accountNumber: String?,

    @ColumnInfo(name = "provider")
    val provider: String?,

    @ColumnInfo(name = "expiry")
    val expiry: String?,

    @ColumnInfo(name = "cvv")
    val cvv: String?,
)