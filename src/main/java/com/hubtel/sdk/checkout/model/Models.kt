package com.hubtel.sdk.checkout.model

data class Wallet(
    val accountNumber: String,
    val expiry: String,
)

class WalletProvider


val Wallet.walletProvider: WalletProvider get() = TODO()