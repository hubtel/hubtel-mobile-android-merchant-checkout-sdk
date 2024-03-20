package com.hubtel.merchant.checkout.sdk.ux.pay.status.confirm

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusViewModel
import com.hubtel.merchant.checkout.sdk.ux.pay.status.finishWithResult
import com.hubtel.merchant.checkout.sdk.ux.pay.status.successful_transaction.TransactionSuccessfulScreen
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity

internal data class ConfirmOrderScreen(
    private val providerName: String?,
    private val config: CheckoutConfig
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel<PaymentStatusViewModel>(
            factory = PaymentStatusViewModel.getViewModelFactory(config.apiKey)
        )

        ConfirmOrderScreenContent(viewModel)
    }

    @Composable
    private fun ConfirmOrderScreenContent(paymentStatusViewModel: PaymentStatusViewModel) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        val uiState by paymentStatusViewModel.uiState
        val orderStatus = uiState.data

        val errorMessage = uiState.error?.asString()

        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }
//        val paymentStatus = remember(orderStatus) { orderStatus?.paymentStatus }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.checkout_confirm_order),
                        style = HubtelTheme.typography.body1
                    )
                },
                actions = {
                    TextButton(onClick = {
                        checkoutActivity?.finishWithResult()
                        recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCancel)
                    }) {
                        Text(
                            text = stringResource(id = R.string.checkout_cancel),
                            color = HubtelTheme.colors.error,
                        )
                    }
                })
        }, bottomBar = {
            Column(
                Modifier
                    .animateContentSize(),
            ) {
                LoadingTextButton(
                    text = stringResource(R.string.checkout_done),
                    onClick = {
                        navigator?.push(
                            TransactionSuccessfulScreen(
                                providerName = providerName,
                                config = config
                            )
                        )
                        // TODO "Implement onClick"
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall)
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
                        painter = painterResource(id = R.drawable.checkout_ic_success_teal),
                        contentDescription = null,
                        modifier = Modifier.size(86.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.checkout_success),
                        style = HubtelTheme.typography.h3,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                    Text(
                        text = "Your payment was successful. Thank you for choosing us",
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )

                }
            }
        }
    }
}