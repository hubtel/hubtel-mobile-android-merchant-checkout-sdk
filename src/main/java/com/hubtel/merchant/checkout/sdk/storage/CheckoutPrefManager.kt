package com.hubtel.merchant.checkout.sdk.storage

import android.content.Context
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PaymentChannel

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

    var mandateId: String?
        set(value) = saveToSharedPref("gmoney.mandate.id", value)
        get() = getSharedPrefString("gmoney.mandate.id")
}