package com.hubtel.merchant.checkout.sdk.platform.analytics.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun getDayOfMonthSuffix(n: Int): String {
    return if (n in 11..13) {
        "th"
    } else when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

/**
 * Formats a [Date] object into to d MMMM, yyyy [String] with the position of
 * day in month indicated.
 *
 * @return formatted [String] represented by the [Date] object.
 *
 *  ### Example:
 * ```
 *   println(Date(1646737397238).toNthDateFormat()) // outputs 8th March 2022
 * ```
 */
fun Date.toNthDateFormat(): String {
    val sdf = SimpleDateFormat("d MMMM yyyy", Locale.US)
    return try {
        val formattedString = sdf.format(this)
        val splitString = formattedString.split("\\s+".toRegex())

        val sb = StringBuilder()
        splitString.forEachIndexed { index, s ->
            if (index == 0 && splitString.size > 1) {
                sb.append(s)
                sb.append(getDayOfMonthSuffix(s.toIntOrNull() ?: 0))
            } else {
                sb.append(s)
            }

            sb.append(" ")
        }

        sb.toString().trim()

    } catch (ex: Exception) {
        this.toString()
    }
}

/**
 * Parses a yyyy-MM-dd HH:mm:ss or yyyy-MM-dd'T'HH:mm:ss [String]
 * to an equivalent [Date] object.
 *
 * @param includeTime indicates the whether the HH:mm:ss part of the
 * date string being parsed should be included in the output [Date]
 * object.
 *
 * @return [Date] object equivalent of the [String] or null if the
 * parsing of the [String] fails.
 */
fun String.parseDateTime(includeTime: Boolean = true): Date? {
    return parseYyyyMMddHHmmss(includeTime) ?: parseYyyyMMddTHHmmss(includeTime)
}

/**
 * Use parseDateTime() instead
 * */
private fun String.parseYyyyMMddHHmmss(includeTime: Boolean): Date? {
    val formatString = StringBuilder().apply {
        append("yyyy-MM-dd")

        if (includeTime) {
            append(" ")
            append("HH:mm:ss")
        }
    }.toString()


    val parseString = if (!includeTime) {
        this.split("\\s+".toRegex()).firstOrNull() ?: this
    } else this

    return try {
        val sdf = SimpleDateFormat(formatString, Locale.US)
        sdf.parse(parseString)
    } catch (ex: Exception) {
        null
    }
}

/**
 * Use parseDateTime() instead
 * */
private fun String.parseYyyyMMddTHHmmss(includeTime: Boolean): Date? {
    val formatString = StringBuilder().apply {
        append("yyyy-MM-dd")

        if (includeTime) {
            append("'T'")
            append("HH:mm:ss")
        }
    }.toString()


    val parseString = if (!includeTime) {
        this.split("'T'".toRegex()).firstOrNull() ?: this
    } else this

    return try {
        val sdf = SimpleDateFormat(formatString, Locale.US)
        sdf.parse(parseString)
    } catch (ex: Exception) {
        null
    }
}

/**
 * Formats to a MMM dd, yyyy [String].
 *
 * @return formatted [String] represented by the [Date] object and null
 * if formatting fails.
 *
 *  ### Example:
 * ```
 *   println(Date().formatMMMddYYYY()) // outputs Feb 15, 2020
 * ```
 */
fun Date.formatMMMddYYYY(): String? {
    return this.format("MMM dd, yyyy")
}

/**
 * Formats to a YYYY-MM-dd HH:mm:ss [String].
 *
 * @return formatted [String] represented by the [Date] object and null
 * if formatting fails.
 *
 *  ### Example:
 * ```
 *   println(Date(1646737397238).formatYyyyMMddHHmmss()) // outputs 2022-03-08 17:45:30
 * ```
 */
fun Date.formatYyyyMMddHHmmss(): String? {
    return this.format("yyyy-MM-dd HH:mm:ss")
}

/**
 * Formats to a H:mm a [String].
 *
 * @return formatted [String] represented by the [Date] object and null
 * if formatting fails.
 *
 *  ### Example:
 * ```
 *   println(Date(1646737397238).formatHmma()) // outputs 11:03 AM
 * ```
 */
fun Date.formatHmma(): String? {
    return this.format("H:mm a")
}

fun Date.formatMMMMdyyyy(): String? {
    return this.format("MMMM d, yyyy")
}

fun Date.format(pattern: String): String? {
    val sdf = SimpleDateFormat(pattern, Locale.US)
    return try {
        sdf.format(this)
    } catch (ex: Exception) {
        null
    }
}

fun Date.isSameDay(other: Date): Boolean {
    return this.format("yyyyMMdd") == other.format("yyyyMMdd")
}

/**
 * @return a [Date] representation of the difference between subtracting
 * the two [Date.getTime] values.
 */
operator fun Date.minus(other: Date): Date {
    return Date(this.time - other.time)
}

/**
 * @return a [Date] representation of the difference between adding
 * the two [Date.getTime] values.
 */
operator fun Date.plus(other: Date): Date {
    return Date(this.time + other.time)
}

operator fun Date.plus(other: Long): Date {
    return Date(this.time + other)
}

operator fun Date.minus(other: Long): Date {
    return Date(this.time - other)
}
