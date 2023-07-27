package com.hubtel.sdk.checkout.platform.model

internal data class Wallet(
    val id: Long,
    val accountNumber: String?,
    val accountName: String?,
    val expiry: String?,
    val cvv: String?,
    val provider: String?
)