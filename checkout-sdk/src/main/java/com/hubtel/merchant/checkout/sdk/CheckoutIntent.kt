package com.hubtel.merchant.checkout.sdk

import android.content.Context
import android.content.Intent
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig

object CheckoutIntent {
    class Builder(private val context: Context) {

        private var apiKey: String? = null
        private var merchantId: String? = null
        private var amount: Double = 0.0
        private var msisdn: String? = null
        private var callbackUrl: String? = null
        private var clientReference: String? = null
        private var description: String? = null

        private var themeConfig: ThemeConfig? = null

        fun setApiKey(apiKey: String): Builder {
            this.apiKey = apiKey
            return this
        }

        fun setMerchantId(id: String): Builder {
            this.merchantId = id
            return this
        }

        fun setClientReference(reference: String): Builder {
            this.clientReference = reference
            return this
        }

        fun setCustomerPhoneNumber(msisdn: String): Builder {
            this.msisdn = msisdn
            return this
        }

        fun setAmount(amount: Double): Builder {
            this.amount = amount
            return this
        }

        fun setCallbackUrl(url: String): Builder {
            this.callbackUrl = url
            return this
        }

        fun setDescription(description: String): Builder {
            this.description = description
            return this
        }

        fun setTheme(config: ThemeConfig): Builder {
            this.themeConfig = config
            return this
        }

        fun build(): Intent {
            require(amount > 0) { "Amount should be greater than zero" }
            require(!apiKey.isNullOrBlank()) { "Api key is required" }
            checkNotNull(msisdn) { "Customer phone number is required" }
            checkNotNull(callbackUrl) { "Callback Url is required" }
            checkNotNull(clientReference) {
                "ClientReference cannot be null. (Unique reference provided by API user) " +
                        "alphanumeric eg. test10652132 "
            }
            checkNotNull(description) { "description cannot be null. (A brief description of transaction)" }

            val config = CheckoutConfig(
                apiKey = apiKey,
                posSalesId = this.merchantId,
                amount = this.amount,
                msisdn = this.msisdn,
                callbackUrl = this.callbackUrl,
                clientReference = this.clientReference,
                description = this.description,
                themeConfig = themeConfig,
            )

            return Intent(context, CheckoutActivity::class.java).apply {
                putExtra(CheckoutActivity.CHECKOUT_CONFIG, config)
            }
        }
    }
}