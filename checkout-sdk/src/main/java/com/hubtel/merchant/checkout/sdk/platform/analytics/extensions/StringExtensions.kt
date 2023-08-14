package com.hubtel.merchant.checkout.sdk.platform.analytics.extensions

fun String.toFriendlyName(): String {
    return this.split("_")
        .joinToString(" ") { x ->
            x.lowercase().replaceFirstChar { it.uppercaseChar() }
        }
}

fun String.toFirebaseEventName(): String {
    return this.split("//s+".toRegex())
        .joinToString("_") { it }
        .lowercase()
}

fun String.toPascalCaseName(): String {
    return this.split("_")
        .joinToString("") { x ->
            x.replaceFirstChar { it.uppercaseChar() }
        }
}