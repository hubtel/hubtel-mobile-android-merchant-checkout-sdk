package com.hubtel.sdk.checkout.storage

import android.content.Context
import com.hubtel.sdk.checkout.ux.pay.order.PaymentChannel

internal class CheckoutPrefManager(context: Context) : BasePrefManager(
    context,
    "com.hubtel.checkout.pref"
) {

    var allowedPaymentChannels: List<PaymentChannel>?
        set(value) = saveToSharedPref("business.allowed.payment.channels", value)
        get() = getSharedPrefObject("business.allowed.payment.channels")

    var customerPhoneNumber: String?
        set(value) = saveToSharedPref("customer.phone.number", value)
        get() = getSharedPrefString("customer.phone.number")
}