package com.hubtel.merchant.checkout.sdk.ux.navigation

import android.content.Context

interface DeeplinkHandler {

    fun matches(link: String): Boolean

    fun handle(context: Context, link: String)

}


//val DeeplinkHandler.APP_BASE_URI: String
//    get() = "hubtel.com"