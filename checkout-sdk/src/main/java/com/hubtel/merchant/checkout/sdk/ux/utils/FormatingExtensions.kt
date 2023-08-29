package com.hubtel.merchant.checkout.sdk.ux.utils

import java.text.DecimalFormat

fun Double?.formatWithDelimiters(): String {
    if (this != null) {
        return DecimalFormat("#,###,##0.00").format(this)
    }

    return 0.0.formatWithDelimiters()
}

fun Double?.formatMoney(
    displayCurrency: String? = null,
    includeDecimals: Boolean = true
): String {
    val formatted = (this ?: 0.0).formatWithDelimiters()
    val integerDecimalParts = formatted.split(".")

    val decimalPart = integerDecimalParts.getOrNull(1) ?: "00"
    if (decimalPart == "00" && !includeDecimals) {
        return "${displayCurrency ?: "GHS"} ${integerDecimalParts[0]}"
    }

    return "${displayCurrency ?: "GHS"} ${integerDecimalParts[0]}.$decimalPart"
}

fun Double?.formatMoneyParts(
    currency: String? = null,
    includeDecimals: Boolean = false
): Triple<String, String, String> {
    val numberParts = this.formatWithDelimiters().split(".")
    val decimalPart = numberParts.getOrNull(1) ?: "00"

    if (decimalPart == "00" && !includeDecimals) {
        return Triple(currency ?: "GHS ", numberParts[0], "")
    }

    return Triple(currency ?: "GHS ", numberParts[0], ".$decimalPart")
}

fun String?.formatMoney(
    displayCurrency: String? = null,
    includeDecimals: Boolean = false
): String {
    if (this != null) {
        val stringAsDouble = this.toDoubleOrNull() ?: 0.0
        return stringAsDouble.formatMoney(displayCurrency, includeDecimals)
    }

    return (0.0).formatMoney(displayCurrency, includeDecimals)
}