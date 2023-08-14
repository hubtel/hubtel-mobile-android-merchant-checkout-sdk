package com.hubtel.merchant.checkout.sdk.platform.analytics.extensions

import android.os.Bundle

internal fun Map<*, *>.toBundle(): Bundle {
    val bundle = Bundle()

    for ((key, value) in this) {
        val keyString = key.toString()

        when (value) {
            is Double -> {
                bundle.putDouble(keyString, value)
            }

            is Int -> {
                bundle.putLong(keyString, value.toLong())
            }

            is Long -> {
                bundle.putLong(keyString, value)
            }

            is Map<*, *> -> {
                bundle.putBundle(keyString, value.toBundle())
            }

            is List<*> -> {
                val bundleArray = value
                    .filterIsInstance<Map<*, *>>()
                    .map { it.toBundle() }.toTypedArray()

                bundle.putParcelableArray(keyString, bundleArray)
            }

            else -> {
                //param values should be <= 100 chars
                bundle.putString(keyString, value.toString().take(100))
            }
        }
    }

    return bundle
}
