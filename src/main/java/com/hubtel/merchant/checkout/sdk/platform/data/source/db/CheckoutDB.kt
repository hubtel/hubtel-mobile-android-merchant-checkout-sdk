package com.hubtel.merchant.checkout.sdk.platform.data.source.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.dao.WalletDao
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet

private const val DB_NAME = "checkout_wallets"
private const val DB_VERSION = 2

@Database(
    version = DB_VERSION,
    exportSchema = false,
    entities = [DbWallet::class]
)
internal abstract class CheckoutDB : RoomDatabase() {

    abstract fun walletDao(): WalletDao

    companion object {

        @Volatile
        private var INSTANCE: CheckoutDB? = null

        fun getInstance(context: Context): CheckoutDB {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context, CheckoutDB::class.java,
                        DB_NAME,
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}