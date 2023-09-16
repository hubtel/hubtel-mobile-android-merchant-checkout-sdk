package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

internal data class CheckoutFee(
    @SerializedName("fees")
    val fees: Double,
    @SerializedName("amountPayable")
    val amountPayable: Double,
    @SerializedName("checkoutType")
    val checkoutType: String
) {
    val getCheckoutType: CheckoutType
        get() = when (checkoutType.lowercase()) {
            CheckoutType.RECEIVE_MONEY_PROMPT.rawValue -> CheckoutType.RECEIVE_MONEY_PROMPT
            CheckoutType.DIRECT_DEBIT.rawValue -> CheckoutType.DIRECT_DEBIT
            CheckoutType.PRE_APPROVAL_CONFIRM.rawValue -> CheckoutType.PRE_APPROVAL_CONFIRM
            else -> CheckoutType.RECEIVE_MONEY_PROMPT
        }
}


enum class CheckoutType(val rawValue: String) {
    RECEIVE_MONEY_PROMPT("receivemoneyprompt"),
    DIRECT_DEBIT("directdebit"),
    PRE_APPROVAL_CONFIRM("preapprovalconfirm")
}

internal data class BusinessInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("contact")
    val contact: String?
)
