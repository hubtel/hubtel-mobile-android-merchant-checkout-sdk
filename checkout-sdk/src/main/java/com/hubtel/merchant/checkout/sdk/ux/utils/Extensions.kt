package com.hubtel.merchant.checkout.sdk.ux.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.toReceiptDateTime(): String {
    val dateFormat = SimpleDateFormat("dd MMM, yyyy • h:mm a", Locale.getDefault())
    return dateFormat.format(this)
}

fun String.removeSpaces(): String {
    return this.replace("\\s".toRegex(), "")
}
