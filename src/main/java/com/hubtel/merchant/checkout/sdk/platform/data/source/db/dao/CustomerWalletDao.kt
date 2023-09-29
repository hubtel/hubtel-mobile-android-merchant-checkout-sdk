package com.hubtel.merchant.checkout.sdk.platform.data.source.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.HubtelWallet

@Dao
internal interface HubtelWalletDao: BaseDao<HubtelWallet> {
    @Query("SELECT * FROM $NAME")
    fun getAllWallets(): List<HubtelWallet>

    @Query("DELETE FROM $NAME")
    fun deleteAll()

    @Query("DELETE FROM $NAME WHERE id = :id")
    fun deleteById(id: String)

    companion object {
        const val NAME = "DbCustomerWallet"
    }
}