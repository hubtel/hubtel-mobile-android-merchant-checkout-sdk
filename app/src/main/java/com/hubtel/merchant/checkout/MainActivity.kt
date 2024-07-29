package com.hubtel.merchant.checkout

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.hubtel.merchant.checkout.sdk.CheckoutIntent
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutStatus
import com.hubtel.merchant.checkout.sdk.ux.shared.BaseActivity
import com.hubtel.merchant.checkout.sdk.ux.theme.ThemeConfig
import java.util.UUID

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { startCheckout() }) {
                    Text(text = "Launch Checkout")
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
            .setApiKey("T0UwajAzcjo5ZjAxMzhkOTk5ZmM0ODMxYjc3MWFhMzEzYTNjMThhNA==")
            .setMerchantId("11684")
            .setCallbackUrl("https://cd81-154-47-25-8.ngrok-free.app/payment-callback")
            .setDescription("Rice with Coleslaw")
            .setClientReference(UUID.randomUUID().toString())
            .setCustomerPhoneNumber("233540975983")
            .setTheme(themeConfig)
            .build()

        startActivityForResult( intent, CHECKOUT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            val status =
                data?.getParcelableExtra<CheckoutStatus?>(CheckoutStatus.CHECKOUT_RESULT)

            status?.isCanceled
            status?.isPaymentSuccessful
            status?.transactionId
            status?.paymentMethod
        }
    }


    companion object{
        const val CHECKOUT_REQUEST_CODE  = 2233
    }
}