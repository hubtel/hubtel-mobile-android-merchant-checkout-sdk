package com.hubtel.merchant.checkout.sdk.ux.pay.status.successful_transaction

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.pay.status.finishWithResult
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity

internal class PaidSuccessScreen : Screen {
    @Composable
    override fun Content() {
        ScreenContent()
    }

    @Composable
    private fun ScreenContent() {

        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(text = "Confirm Order")
            }, actions = {
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = "Cancel", color = HubtelTheme.colors.error)
                }
            })
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {
                LoadingTextButton(
                    text = stringResource(R.string.checkout_done), onClick = {
                        checkoutActivity?.finishWithResult()
                        recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                    }, modifier = Modifier.fillMaxWidth().padding(Dimens.paddingSmall)
                )
            }
        }) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(Dimens.paddingDefault),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.width(256.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = com.hubtel.merchant.checkout.sdk.R.drawable.checkout_ic_success_teal),
                        contentDescription = null,
                        modifier = Modifier.size(86.dp)
                    )

                    Text(
                        text = stringResource(id = com.hubtel.merchant.checkout.sdk.R.string.checkout_success),
                        style = HubtelTheme.typography.h3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                    Text(
                        text = stringResource(id = com.hubtel.merchant.checkout.sdk.R.string.checkout_success_message),
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )

                }
            }
        }
    }
}