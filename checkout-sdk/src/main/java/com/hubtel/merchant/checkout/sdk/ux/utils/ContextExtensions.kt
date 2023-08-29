package com.hubtel.merchant.checkout.sdk.ux.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.addToClipboard(
    clipboardText: String,
    label: String = "text",
) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText(label, clipboardText)
    clipboard?.setPrimaryClip(clip)
}

fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        this.packageManager.getPackageInfo(packageName, 0)
        true
    } catch (ex: PackageManager.NameNotFoundException) {
        false
    }
}

