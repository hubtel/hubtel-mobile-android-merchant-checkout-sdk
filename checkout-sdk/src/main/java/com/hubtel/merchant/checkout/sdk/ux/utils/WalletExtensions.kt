package com.hubtel.merchant.checkout.sdk.ux.utils

import com.hubtel.merchant.checkout.sdk.platform.data.source.db.model.DbWallet
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet

internal fun DbWallet.toWallet(): Wallet = Wallet(
    id = this.id,
    accountName = this.accountName,
    accountNumber = this.accountNumber,
    expiry =  this.expiry,
    cvv =  this.cvv,
    provider = this.provider
)