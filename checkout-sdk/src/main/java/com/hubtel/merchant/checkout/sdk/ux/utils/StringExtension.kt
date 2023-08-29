package com.hubtel.merchant.checkout.sdk.ux.utils

import java.util.*

/**
 * Returns a new string with the first character of the string capitalized.
 * Calling this function on a string which already starts with a capital letter will
 * have no visible effect.
 *
 * ### Example:
 * ```
 * println("onions".capitalizeFirst()) // outputs Onions
 * println("ONions".capitalize()) // outputs ONions"
 * ```
 */
fun String.capitalizeFirst(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault())
        else it.toString()
    }
}

fun String.indexFirstNonWhiteSpace(startIndex: Int = 0): Int {
    for (i in (startIndex..this.length.minus(1))) {
        if (this[i].isWhitespace().not())
            return i
    }
    return -1
}

fun String.indexLastNonWhiteSpace(): Int {
    for (i in this.indices) {
        if (this[this.length - (i + 1)].isWhitespace().not())
            return this.length - (i + 1)
    }
    return -1
}

/**
 * Returns a new string with the first character of the string capitalized.
 *
 * ### Example:
 * ```
 * println("onions".capitalizeFirstCase()) // outputs Onions
 * println("ONions".capitalizeFirstCase()) // outputs Onions
 * println("ONions For Me".capitalizeFirstCase()) // outputs Onions for me
 * ```
 */
fun String.capitalizeFirstCase(cleanSpace: Boolean = true): String {
    return if (this.isEmpty())
        this
    else if (cleanSpace && this.isBlank())
        ""
    else if (cleanSpace)
        this.replace("\\s+".toRegex(), " ").trim().let {
            "${it[0].uppercase()}${it.substring(1, it.length).lowercase()}"
        }
    else this.lowercase().let {
        val index = it.indexFirstNonWhiteSpace()
        if (index < 0) it else {
            this.lowercase().let { s ->
                "${s.substring(0, index)}${s[index].uppercase()}${s.substring(index + 1, s.length)}"
            }
        }
    }
}

/**
 * Returns a new string with the each substring from splitting by ' ' capitalized and joined.
 *
 * ### Example:
 * ```
 * println("onions for me".capitalizeWordCase()) // outputs Onions For Me
 * println("ONIONS FOR ME".capitalizeWordCase()) // outputs Onions For Me
 * println("ONions".capitalizeWordCase()) // outputs Onions
 * ```
 */
fun String.capitalizeWordCase(cleanSpace: Boolean = true): String {
    return if (this.isEmpty())
        this
    else if (cleanSpace && this.isBlank())
        ""
    else if (cleanSpace)
        this.trim().split("\\s+".toRegex()).joinToString(" ") { it.capitalizeFirstCase() }
    else this.lowercase().toCharArray().let {
        var capitalizedString = ""
        var prev: Char? = null
        it.forEach { c ->
            capitalizedString += if (c.isWhitespace().not() && (prev == null || prev?.isWhitespace() == true)) {
                c.uppercase()
            } else {
                c.lowercase()
            }
            prev = c
        }
        capitalizedString
    }
}

/**
 * Returns a new string with the each substring from splitting by '.' capitalized and joined.
 *
 * ### Example:
 * ```
 * println("i like her. she likes me".capitalizeSentenceCase()) // outputs I like her. She likes me
 * println("I LIKE HER. She Likes me.".capitalizeSentenceCase()) // outputs I like her. She likes me.
 * println("I Like Her".capitalizeSentenceCase()) // outputs I like her."
 * ```
 */
fun String.capitalizeSentenceCase(cleanSpace: Boolean = true): String {
    return if (this.isEmpty())
        this
    else if (cleanSpace && this.isBlank())
        ""
    else if (cleanSpace)
        this.split(".").joinToString(separator = ". ") { it.capitalizeFirstCase() }
            .replace("\\s+".toRegex(), " ").trim()
    else this.lowercase().let {
        this.split(".").joinToString(separator = ". ") { it.capitalizeFirstCase() }
    }
}

/**
 * Returns a new string with the each substring from splitting by '.' capitalized and joined.
 *
 * ### Example:
 * ```
 * println("bill kwaku ansah-inkoom".capitalizeNameWordCase()) // outputs Bill Kwaku Ansah-Inkoom
 * println("Bill Kwaku Ansah-inkoom".capitalizeNameWordCase()) // outputs Bill Kwaku Ansah-Inkoom
 * println("Bill inkoom".capitalizeNameWordCase()) // outputs Bill Inkoom
 * ```
 */
fun String.capitalizeNameWordCase(delimiters: List<String> = listOf(" ", "-", ".")): String {
    var capitalizedString = ""
    var prev: Char? = null
    this.forEach { c ->
        capitalizedString += if (c.isWhitespace().not() && (prev == null || prev?.toString() in delimiters)) {
            c.uppercase()
        } else {
            c.lowercase()
        }
        prev = c
    }
    return capitalizedString.trim()
}