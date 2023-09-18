package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

data class CheckoutInfo(
    @SerializedName("transactionId")
    val transactionId: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("customData")
    val customData: String?,
    @SerializedName("jwt")
    val jwt: String?,
    @SerializedName("preapprovalStatus")
    val preapprovalStatus: String
)

/*{
    val getPreapprovalStatus: PreapprovalStatus
        get() = when (preapprovalStatus.lowercase()) {
            "approved" -> PreapprovalStatus.APPROVED
            "pending" -> PreapprovalStatus.PENDING
        }
}*/

/*

val getCheckoutType: CheckoutType
        get() = when (checkoutType.lowercase()) {
            CheckoutType.RECEIVE_MONEY_PROMPT.rawValue -> CheckoutType.RECEIVE_MONEY_PROMPT
            CheckoutType.DIRECT_DEBIT.rawValue -> CheckoutType.DIRECT_DEBIT
            CheckoutType.PRE_APPROVAL_CONFIRM.rawValue -> CheckoutType.PRE_APPROVAL_CONFIRM
            else -> CheckoutType.RECEIVE_MONEY_PROMPT
        }
 */

enum class PreapprovalStatus(val rawValue: String){
    APPROVED("approved"),
    PENDING("pending"),
}


data class MomoCheckoutInfo(
    @SerializedName("charges")
    val charges: Double?,
    @SerializedName("amount")
    val amount: Double?,
    @SerializedName("amountAfterCharges")
    val amountAfterCharges: Double?,
    @SerializedName("amountCharged")
    val amountCharged: Double?,
    @SerializedName("deliveryFee")
    val deliveryFee: Double?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("clientReference")
    val clientReference: String?,
)


/*

let transactionId: String?
      charges: Double?
      amount: Double?
      amountAfterCharges: Double?
      amountCharged: Double?
      deliveryFee: Double?
      description: String?
      clientReference: String?

 */