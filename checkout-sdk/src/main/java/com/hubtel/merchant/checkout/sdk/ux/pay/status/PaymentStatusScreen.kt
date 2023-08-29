package com.hubtel.merchant.checkout.sdk.ux.pay.status

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseFailedEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordPurchaseFailedEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentStatus
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.platform.model.toWalletProvider
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutStatus
import com.hubtel.merchant.checkout.sdk.ux.pay.order.channelName
import com.hubtel.merchant.checkout.sdk.ux.pay.order.toPurchaseOrderItem
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity
import com.hubtel.merchant.checkout.sdk.ux.utils.formatTime
import kotlinx.coroutines.delay

internal data class PaymentStatusScreen(
    private val providerName: String?,
    private val config: CheckoutConfig
) : Screen {

    @Composable
    override fun Content() {
        val viewModel = viewModel<PaymentStatusViewModel>(
            factory = PaymentStatusViewModel.getViewModelFactory(config.apiKey)
        )

        PaymentStatusScreenContent(viewModel)
    }

    @Composable
    private fun PaymentStatusScreenContent(
        paymentStatusViewModel: PaymentStatusViewModel,
    ) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        val uiState by paymentStatusViewModel.uiState
        val orderStatus = uiState.data

        val errorMessage = uiState.error?.asString()

        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }
        val walletProvider = remember(providerName) { providerName?.toWalletProvider() }
        val paymentStatus = remember(orderStatus) { orderStatus?.paymentStatus }

        val beforeInitialLoad by remember(uiState) {
            derivedStateOf {
                uiState.data == null && !uiState.isLoading
            }
        }

        val isLoading by remember(uiState) {
            derivedStateOf { uiState.isLoading }
        }

        val statusCheckCompleted by remember(uiState) {
            derivedStateOf { orderStatus != null && !isLoading }
        }

        var countDownMillis by remember { mutableStateOf(0L) }

        val isCountingDown by remember(countDownMillis) {
            derivedStateOf { countDownMillis > 0 }
        }

        var showChangeWalletDialog by remember { mutableStateOf(false) }

        val paymentStatusIconRes = remember(paymentStatus) {
            when (paymentStatus) {
                PaymentStatus.UNPAID -> R.drawable.checkout_ic_alert_red
                PaymentStatus.PAID -> R.drawable.checkout_ic_success
                else -> R.drawable.checkout_ic_pending
            }
        }

        val paymentStatusMessage = remember(paymentStatus) {
            when (paymentStatus) {
                PaymentStatus.UNPAID,
                PaymentStatus.PAID -> orderStatus?.invoiceStatus

                else -> context.getString(R.string.checkout_payment_being_processed_msg)
            } ?: ""
        }

        HBScaffold(
            backgroundColor = HubtelTheme.colors.uiBackground2,
            topBar = {
                HBTopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.checkout_processing_payment),
                            style = HubtelTheme.typography.h3,
                        )
                    },
                    actions = if (paymentStatus != PaymentStatus.PAID) {
                        {
                            TextButton(
                                onClick = {
                                    checkoutActivity?.finishWithResult()
                                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCancel)
                                },
                            ) {
                                Text(
                                    text = stringResource(R.string.checkout_core_cancel),
                                    color = CheckoutTheme.colors.colorPrimary,
                                )
                            }
                        }
                    } else null
                )
            },
            bottomBar = {
                Column(
                    Modifier
                        .padding(Dimens.paddingDefault)
                        .animateContentSize(),
                ) {
                    when {
                        beforeInitialLoad -> {
                            LoadingTextButton(
                                text = stringResource(R.string.checkout_i_have_paid),
                                onClick = {
                                    paymentStatusViewModel.checkPaymentStatus(config)
                                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapIHavePaid)
                                },
                                loading = isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        paymentStatus == PaymentStatus.PAID -> {
                            LoadingTextButton(
                                text = stringResource(R.string.checkout_done),
                                onClick = {
                                    checkoutActivity?.finishWithResult(orderStatus, paymentStatus)
                                    recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        paymentStatus == PaymentStatus.UNPAID -> {
                            LoadingTextButton(
                                text = stringResource(R.string.checkout_done),
                                onClick = {
                                    checkoutActivity?.finishWithResult()
//                                    onPaymentComplete(false)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        paymentStatus == PaymentStatus.PENDING -> {
                            LoadingTextButton(
                                text = if (isCountingDown) {
                                    val time = countDownMillis.formatTime()
                                    "${stringResource(R.string.checkout_check_again)} ($time)"
                                } else stringResource(R.string.checkout_check_again),
                                onClick = {
                                    if (!isCountingDown) {
                                        countDownMillis = CHECK_COUNTDOWN_TIME
//                                        tapCount++

                                        paymentStatusViewModel.checkPaymentStatus(config)
                                        recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCheckAgain)
                                    }
                                },
                                enabled = !isCountingDown,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(Dimens.paddingDefault),
                verticalArrangement = Arrangement.Center
            ) {

                // first time before user attempts to verify order status
                if (beforeInitialLoad) {
                    Image(
                        painter = painterResource(R.drawable.checkout_ic_receipt_payment),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = Dimens.paddingLarge)
                            .size(90.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = stringResource(R.string.checkout_other_bill_prompt_msg),
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(vertical = Dimens.spacingDefault)
                    )
                }

                // check payment status request in flight
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(bottom = Dimens.paddingLarge)
                        )

                        Text(
                            text = stringResource(R.string.checkout_checking_status),
                            style = HubtelTheme.typography.h3,
                        )
                    }
                }

                if (!isLoading && orderStatus != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(paymentStatusIconRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = Dimens.paddingLarge),
                        )

                        Text(
                            text = paymentStatusMessage,
                            style = HubtelTheme.typography.body2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                        )
                    }
                }

                if (walletProvider == WalletProvider.MTN
                    && paymentStatus != PaymentStatus.PAID
                    && !uiState.isLoading
                ) {
                    MTNPromptApprovalGuide(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                    )
                }
            }
        }


        if (showChangeWalletDialog) {
            CheckoutMessageDialog(
                onDismissRequest = { },
                painter = painterResource(R.drawable.checkout_ic_alert_red),
                message = stringResource(R.string.checkout_new_transactions),
                positiveText = stringResource(R.string.checkout_yes),
                negativeText = stringResource(R.string.checkout_cancel),
                onPositiveClick = {
                    showChangeWalletDialog = false
                    navigator?.pop()
                },
                onNegativeClick = { showChangeWalletDialog = false },
//                properties = DialogProperties(
//                    dismissOnBackPress = false,
//                    dismissOnClickOutside = false
//                )
            )
        }

        // sets the countdown timer for the check again button if
        // the user has checked status and payment is not successful
        if (statusCheckCompleted) {
            LaunchedEffect(uiState) {

                if (uiState.isLoading || !uiState.hasData) return@LaunchedEffect

                when (paymentStatus) {
                    PaymentStatus.UNPAID -> {
                        val orderItems = listOf(config.toPurchaseOrderItem())

                        recordPurchaseFailedEvent(
                            PurchaseFailedEvent(
                                amount = orderStatus?.transactionAmount ?: 0.0,
                                errorMessage = errorMessage,
                                paymentType = walletProvider?.provider,
                                paymentChannel = walletProvider?.channelName,
                                purchaseOrderItems = orderItems,
                                purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                                purchaseOrderProviders = orderItems.map { it.provider ?: "" }
                            )
                        )
                    }

                    PaymentStatus.PAID -> {
                        recordCheckoutEvent(
                            CheckoutEvent.CheckoutPaymentSuccessfulViewPagePaymentSuccessful
                        )

                        val orderItems = listOf(config.toPurchaseOrderItem())

                        recordPurchaseEvent(
                            PurchaseEvent(
                                orderId = orderStatus?.clientReference,
                                amount = orderStatus?.transactionAmount ?: 0.0,
                                paymentType = walletProvider?.provider,
                                paymentChannel = walletProvider?.channelName,
                                purchaseOrderItems = orderItems,
                                purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                                purchaseOrderProviders = orderItems.map { it.provider ?: "" }
                            )
                        )
                    }

                    else -> {}
                }
            }
        }

        if (countDownMillis > 0) {
            LaunchedEffect(countDownMillis) {
                delay(1000L)
                countDownMillis -= 1000L
            }
        }

        LaunchedEffect(Unit) {
            recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusViewPageCheckAgain)
        }

        BackHandler {
            checkoutActivity?.finishWithResult()
        }
    }


    @Composable
    private fun MTNPromptApprovalGuide(
        modifier: Modifier
    ) {
        Column(modifier) {
            Text(
                text = stringResource(R.string.checkout_prompt_not_received),
                color = Color.Gray,
                style = HubtelTheme.typography.h4,
            )
            Text(
                text = stringResource(R.string.checkout_follow_steps),
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )

            Text(
                buildAnnotatedString {
                    append("1. Dial *170# and select Option 6, ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("My Wallet.")
                    }
                },
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )

            Text(
                buildAnnotatedString {
                    append("2. Select Option 3 for, ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("My Approvals.")
                    }
                },
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )

            Text(
                text = stringResource(R.string.checkout_enter_pin),
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )

            Text(
                text = stringResource(R.string.checkout_pending_approval),
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )

            Text(
                text = stringResource(R.string.checkout_prompt_pay),
                color = Color.Gray,
                style = HubtelTheme.typography.body2,
            )
        }
    }

    companion object {
        private const val CHECK_COUNTDOWN_TIME = 5000L
    }
}

internal fun CheckoutActivity.finishWithResult(
    orderStatus: TransactionStatusInfo? = null,
    paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
) {
    submitCheckoutResult(
        CheckoutStatus(
            transactionId = orderStatus?.transactionId,
            isCanceled = orderStatus == null,
            isPaymentSuccessful = paymentStatus == PaymentStatus.PAID
        )
    )
}

