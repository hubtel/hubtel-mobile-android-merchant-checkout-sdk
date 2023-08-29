package com.hubtel.merchant.checkout.sdk.ux.utils

import java.util.concurrent.TimeUnit

/**
 * Converts a [Long] millisecond representation of time
 * into an HH:mm:ss formatted time string if [enableHourFormat] is true else,
 * the output will be of the format mm:ss */
fun Long.formatTime(enableHourFormat: Boolean = false): String {
    val greaterThanHour = this > 3_600_000L && enableHourFormat

    val formatter = if (greaterThanHour) "%1$02d:%2$02d:%3$02d"
    else "%1$02d:%2$02d"

    return if (greaterThanHour) {
        String.format(
            formatter,
            TimeUnit.MILLISECONDS.toHours(this),
            TimeUnit.MILLISECONDS.toMinutes(this) % 60,
            TimeUnit.MILLISECONDS.toSeconds(this) % 60,
        )
    } else {
        String.format(
            formatter,
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) % 60,
        )
    }
}