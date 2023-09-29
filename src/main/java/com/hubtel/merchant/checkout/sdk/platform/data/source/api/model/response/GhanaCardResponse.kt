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

    @SerializedName("birthday"/*, alternate = ["dateofbirth"]*/)
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
) {
    val getCardStatus: CardStatus
        get() = when(status?.lowercase()) {
            CardStatus.VERIFIED.rawValue -> CardStatus.VERIFIED
            CardStatus.UN_VERIFIED.rawValue -> CardStatus.UN_VERIFIED
            else -> CardStatus.UN_VERIFIED
        }
}

enum class CardStatus(val rawValue: String) {
    VERIFIED("verified"),
    UN_VERIFIED("unverified")
}