package com.hubtel.sdk.checkout.platform.data.source.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hubtel.core.storage.db.dao.BaseDao
import com.hubtel.sdk.checkout.platform.data.source.db.model.DbWallet
import kotlinx.coroutines.flow.Flow

@Dao
internal interface WalletDao : BaseDao<DbWallet> {

    @Query("SELECT * FROM $NAME WHERE lower(type) != 'card' OR isLocalWallet")
    fun getAllWallets(): List<DbWallet>

    @Query("SELECT * FROM $NAME WHERE lower(type) != 'card' OR isLocalWallet")
    fun getWalletsAsFlow(): Flow<List<DbWallet>>

    @Query("SELECT * FROM $NAME WHERE lower(type) != 'card' OR isLocalWallet")
    fun getWalletsAsLiveData(): LiveData<List<DbWallet>>

    @Query("DELETE FROM $NAME")
    fun deleteAll()

    @Query("DELETE FROM $NAME WHERE id = :id")
    fun deleteById(id: String)

//   fun insertWallets(newWallets: List<Wallet>) {
//        val userWallets = newWallets
//            .filter { it.type?.lowercase() != "card" }
//            .map { it.toDbWallet() }
//            .toTypedArray()
//
//        insert(*userWallets)
//    }

    companion object {
        const val NAME = "Wallet"
    }
}