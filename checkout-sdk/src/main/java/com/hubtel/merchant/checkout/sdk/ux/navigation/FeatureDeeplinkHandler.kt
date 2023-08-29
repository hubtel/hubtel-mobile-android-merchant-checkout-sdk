package com.hubtel.merchant.checkout.sdk.ux.navigation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

abstract class FeatureDeeplinkHandler() : DeeplinkHandler {

    protected inline fun <reified T> Context.launchActivity(
        link: String,
        intentPacking: ((Intent) -> Unit) = {},
    ) {
        val intent = Intent(this, T::class.java).apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_BROWSABLE)
            addCategory(Intent.CATEGORY_DEFAULT)

            data = try {
                link.toUri()
            } catch (ex: Exception) {
                null
            }
        }

        intentPacking.invoke(intent)
        startActivity(intent)
    }
}