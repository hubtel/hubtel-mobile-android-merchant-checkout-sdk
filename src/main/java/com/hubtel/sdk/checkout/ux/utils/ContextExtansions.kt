package com.hubtel.sdk.checkout.ux.utils

import android.content.Context
import android.util.TypedValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

internal fun Context.getAppColorPrimary(): Color? {
    val typedValue = TypedValue()
    val foundThemeColor = theme.resolveAttribute(
        android.R.attr.colorPrimary, typedValue, true
    )

    return if(foundThemeColor) {
        val androidColor = ContextCompat.getColor(this, typedValue.resourceId)
        Color(androidColor)
    } else null
}