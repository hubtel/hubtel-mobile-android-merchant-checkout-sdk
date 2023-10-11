package com.hubtel.merchant.checkout.sdk.ux.model

import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig
import java.io.Serializable

internal data class CheckoutConfig(
    val apiKey: String?,
    val posSalesId: String?,
    val amount: Double,
    val msisdn: String?,
    val callbackUrl: String?,
    val clientReference: String?,
    val description: String?,
    val themeConfig: ThemeConfig?,
) : Serializable