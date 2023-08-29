package com.hubtel.merchant.checkout.sdk.ux.shared

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri

abstract class BaseFeatureResultContract<I, O> : ActivityResultContract<I, O>() {

    fun buildIntent(
        context: Context,
        activity: Class<out BaseActivity>,
        deepLink: String? = null
    ): Intent {
        return if (deepLink != null) {
            Intent(
                Intent.ACTION_VIEW, deepLink.toUri(),
                context, activity
            )
        } else Intent(context, activity)
    }
}