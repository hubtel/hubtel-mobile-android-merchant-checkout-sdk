package com.hubtel.merchant.checkout

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.hubtel.merchant.checkout.sdk.CheckoutIntent
import com.hubtel.merchant.checkout.sdk.ux.shared.BaseActivity
import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig
import java.util.UUID

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LazyColumn {
                item {
                    Text(text = "Checkout", modifier = Modifier.clickable {
                        startCheckout()
                    })
                }
            }
        }
    }

    private fun startCheckout() {
        val themeConfig = ThemeConfig(
            primaryColor = ContextCompat.getColor(this, R.color.black)
        )

        val intent = CheckoutIntent.Builder(this)
            .setAmount(0.1)
//            .setApiKey("clJEOG5ndzpmNWM5YjhmNzViNWQ0ZmQ2OWIzZTM4ZTMxNDNmMjM5MA==")
//            .setMerchantId("2020492")
            .setApiKey("T0UwajAzcjo5ZjAxMzhkOTk5ZmM0ODMxYjc3MWFhMzEzYTNjMThhNA==")
            .setMerchantId("11684")
            .setCallbackUrl("https://cd81-154-47-25-8.ngrok-free.app/payment-callback")
            .setDescription("Rice with Coleslaw")
            .setClientReference(UUID.randomUUID().toString())
            .setCustomerPhoneNumber("233556236739")
            .setTheme(themeConfig)
            .build()

        startActivity(intent)
    }
}