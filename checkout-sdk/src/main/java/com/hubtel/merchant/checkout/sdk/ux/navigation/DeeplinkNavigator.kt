package com.hubtel.merchant.checkout.sdk.ux.navigation

import android.content.Context

interface DeeplinkNavigator {

    fun navigate(context: Context, link: String): Boolean

}