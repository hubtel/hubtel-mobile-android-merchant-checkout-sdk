package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

internal data class CheckoutFee(
    @SerializedName("amount")
    val feeAmount: Double?,
    @SerializedName("name")
    val feeName: String?,
)

/*

  "message": "fees not set under given conditions",
  "responseCode": "0001",
  "code": 404,
  "data": false,
  "subCode": 0,
  "errors": null

 */

internal  data class  BusinessInfo(
    @SerializedName("name")
    val name: String?,
    @SerializedName("contact")
    val contact: String?
)
