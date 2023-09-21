package com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response

import com.google.gson.annotations.SerializedName

data class GhanaCardResponse(
    @SerializedName("status")
    val status: String?,

    @SerializedName("fullname")
    val fullName: String?,

    @SerializedName("surname")
    val surname: String?,

    @SerializedName("othernames")
    val otherNames: String?,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("motherName")
    val motherName: String?,

    @SerializedName("fatherName")
    val fatherName: String?,

    @SerializedName("region")
    val region: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("nationalId")
    val nationalID: String?
)