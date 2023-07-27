package com.hubtel.sdk.checkout.ux

import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import com.hubtel.sdk.checkout.platform.analytics.AnalyticsUtils
import com.hubtel.core_ui.shared.BaseActivity
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.sdk.checkout.ux.model.CheckoutConfig
import com.hubtel.sdk.checkout.ux.model.CheckoutStatus
import com.hubtel.sdk.checkout.ux.pay.order.PayOrderScreen
import com.hubtel.sdk.checkout.ux.theme.CheckoutColors
import com.hubtel.sdk.checkout.ux.theme.ProvideCheckoutColors
import com.hubtel.sdk.checkout.ux.utils.getAppColorPrimary

internal class CheckoutActivity : BaseActivity() {

    private val checkoutConfig by lazy {
        intent.getParcelableExtra<CheckoutConfig>(CHECKOUT_CONFIG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnalyticsUtils.init(this)
    }

    @Composable
    override fun RootContent() {
        val config = checkoutConfig ?: return

        ProvideCheckoutColors {
            Navigator(PayOrderScreen(config))
        }
    }

    @Composable
    private fun rememberColorConfigPair(): Pair<Color, Color> {
        val defaultPrimaryColor = HubtelTheme.colors.colorPrimary
        return remember(checkoutConfig) {
            val primaryColor = checkoutConfig?.themeConfig
                ?.primaryColor?.let { Color(it) }
                ?: getAppColorPrimary()
                ?: defaultPrimaryColor

            val accentColor = primaryColor.copy(alpha = 0.3f)

            primaryColor to accentColor
        }
    }

    @Composable
    private fun ProvideCheckoutColors(content: @Composable () -> Unit) {
        val colorPair = rememberColorConfigPair()

        ProvideCheckoutColors(
            colors = CheckoutColors(
                colorPrimary = colorPair.first,
                colorAccent = colorPair.second
            ),
            content = content
        )
    }

    fun submitCheckoutResult(status: CheckoutStatus) {
        val checkoutResult = Intent().apply {
            putExtra(CheckoutStatus.CHECKOUT_RESULT, status)
        }

        setResult(RESULT_OK, checkoutResult)
        finish()
    }

    companion object {
        const val CHECKOUT_CONFIG = "hubtel.checkout.config"
    }
}