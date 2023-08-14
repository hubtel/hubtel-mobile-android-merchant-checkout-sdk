package com.hubtel.merchant.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.hubtel.core_ui.shared.BaseActivity
import com.hubtel.merchant.checkout.sdk.CheckoutIntent
import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig
import java.util.UUID

class MainActivity : BaseActivity() {

    @Composable
    override fun RootContent() {
        super.RootContent()

        LazyColumn {
            item {
                Text(
                    text = "Checkout",
                    modifier = Modifier
                        .clickable { startCheckout() }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }

    private fun startCheckout() {
        val themeConfig = ThemeConfig(
            primaryColor = ContextCompat.getColor(this, R.color.bright_red)
        )

        val intent = CheckoutIntent.Builder(this)
            .setAmount(0.1)
            .setApiKey("T0UwajAzcjo5ZjAxMzhkOTk5ZmM0ODMxYjc3MWFhMzEzYTNjMThhNA==")
            .setCallbackUrl("https://cd81-154-47-25-8.ngrok-free.app/payment-callback")
            .setDescription("Rice with Coleslaw")
            .setMerchantId("11684")
            .setClientReference(UUID.randomUUID().toString())
            .setCustomerPhoneNumber("233540343395")
            .setTheme(themeConfig)
            .build()

        startActivity(intent)
    }
}