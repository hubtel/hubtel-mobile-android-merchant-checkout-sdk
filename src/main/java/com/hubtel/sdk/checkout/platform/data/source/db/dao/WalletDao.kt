package com.hubtel.sdk.checkout.platform.data.source.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hubtel.sdk.checkout.platform.data.source.db.model.DbWallet
import kotlinx.coroutines.flow.Flow

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