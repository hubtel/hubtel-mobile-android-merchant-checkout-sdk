package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request

import com.google.gson.annotations.SerializedName
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse

data class CardReq(
    @SerializedName("CustomerMobileNumber")
    val customerMobileNumber: String? = null,
    @SerializedName("VerifyIdType")
    val verifyIdType: String? = null,
    @SerializedName("NationalId")
    val nationalId: String? = null,
    @SerializedName("Fullname")
    val fullName: String? = null,
    @SerializedName("DateOfBirth")
    val dateOfBirth: String? = null,
    @SerializedName("gender")
    val gender: String? = null,
    @SerializedName("email")
    val email: String? = null
)

fun GhanaCardResponse.toCardReq(): CardReq {
    return CardReq(
        customerMobileNumber = phone,
        verifyIdType = "NationalId",
        nationalId = nationalID,
        fullName = fullName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        email = ""
    )
}