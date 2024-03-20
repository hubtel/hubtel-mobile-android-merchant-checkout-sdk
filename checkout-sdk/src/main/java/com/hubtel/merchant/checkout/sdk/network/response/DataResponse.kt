package com.hubtel.merchant.checkout.sdk.network.response

import com.google.gson.annotations.SerializedName

data class DataResponse2<T>(
    @SerializedName("data")
    val data: T?,

    @SerializedName("developer_message")
    val developerMessage: String?,

    @SerializedName("error")
    val error: Boolean = false,

    @SerializedName("message")
    val message: String?,

    @SerializedName("status", alternate = ["code"])
    val code: String?
)

/*

{
    "message": "Success",
    "responseCode": "0000",
    "code": 200,
    "data": {
        "businessId": "gershon",
        "businessName": "Platform Shop",
        "businessLogoUrl": "https://dev-hubtel.s3-eu-west-1.amazonaws.com/images/373efe0a7efb467fbfb69529a0d06ed4-05032021135018.jpeg",
        "channels": [
            "cardnotpresent-mastercard",
            "mtn-gh",
            "hubtel-gh",
            "cardpresent-visa",
            "cardpresent-mastercard",
            "cardnotpresent-visa",
            "tigo-gh",
            "vodafone-gh"
        ]
    },
    "subCode": 0,
    "errors": null
}

 */


data class DataResponse<T>(
    @SerializedName("data")
    val data: T?,

    @SerializedName("errors")
    val errors: List<String>? = emptyList(),

    @SerializedName("message")
    val message: String?,

    @SerializedName("code", alternate = ["status"])
    val code: String?,

    @SerializedName("responseCode")
    val responseCode: String?
)