package com.hubtel.merchant.checkout.sdk.ux.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

/**
 * Checks if user device Android Sdk is greater than or equal
 * to [value] before executing [block].
 *
 * @param value an integer value from build [Build.VERSION_CODES] for which we check against.
 * @return type value [T], is the value returned by calling block. If block wasn't executed,
 * due to failed condition, then null is returned.
 */
@ChecksSdkIntAtLeast(parameter = 0, lambda = 1)
inline fun <T> fromAndroidVersion(value: Int, block: () -> T?): T? {
    return if (Build.VERSION.SDK_INT >= value) {
        block()
    } else null
}


/**
 * Retrieves the Google Cloud Platform (GCP) key accessed from the manifest.
 *
 * @return A string representing the GCP key.
 */
fun Context.getGCPKey(): String {
    return try {
        val appInfo = applicationContext.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        appInfo.metaData["GCP_KEY"].toString()
    } catch (e:PackageManager.NameNotFoundException) {
        ""
    }
}