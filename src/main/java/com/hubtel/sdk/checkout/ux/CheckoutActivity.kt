package com.hubtel.sdk.checkout.ux

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.hubtel.core_ui.shared.BaseActivity
import com.hubtel.sdk.checkout.ux.pay.order.PayOrderScreen
import com.hubtel.sdk.checkout.ux.pay.order.PayOrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class CheckoutActivity : BaseActivity() {

    private val viewModel by viewModels<PayOrderViewModel> { PayOrderViewModel.Factory }

    @Composable
    override fun RootContent() {
//        config?.let { checkoutConfig ->
//            CheckoutNavHost(
//                checkoutConfig,
//                onFinish = { finish() },
//                onCheckoutCompleted = this::submitCheckoutResult
//            )
//        }
        PayOrderScreen(viewModel)
    }

    private fun submitCheckoutResult(paymentSuccessful: Boolean) {
        val checkoutResult = Intent().apply {
            putExtra(CHECKOUT_RESULT, paymentSuccessful)
        }

        setResult(RESULT_OK, checkoutResult)
        finish()
    }

    companion object {
        const val CHECKOUT_CONFIG = "checkout.config"
        const val CHECKOUT_RESULT = "checkout.config"
    }
}