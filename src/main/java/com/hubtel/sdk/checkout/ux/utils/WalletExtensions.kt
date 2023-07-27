package com.hubtel.sdk.checkout.ux.utils

import com.hubtel.sdk.checkout.platform.data.source.db.model.DbWallet
import com.hubtel.sdk.checkout.platform.model.Wallet

internal fun DbWallet.toWallet(): Wallet = Wallet(
    id = this.id,
    accountName = this.accountName,
    accountNumber = this.accountNumber,
    expiry =  this.expiry,
    cvv =  this.cvv,
    provider = this.provider
)