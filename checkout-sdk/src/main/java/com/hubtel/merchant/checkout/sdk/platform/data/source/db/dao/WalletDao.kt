package com.hubtel.merchant.checkout.sdk.platform.data.source.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet

@Dao
internal interface WalletDao : BaseDao<DbWallet> {

    @Query("SELECT * FROM $NAME")
    fun getAllWallets(): List<DbWallet>

    @Query("DELETE FROM $NAME")
    fun deleteAll()

    @Query("DELETE FROM $NAME WHERE id = :id")
    fun deleteById(id: String)

    companion object {
        const val NAME = "Wallet"
    }
}