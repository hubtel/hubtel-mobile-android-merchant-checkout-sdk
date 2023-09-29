package com.hubtel.merchant.checkout.sdk.ux.pay.status

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.core_utils.extensions.formatter.formatTime
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.PurchaseFailedEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordPurchaseFailedEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentStatus
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.platform.model.toWalletProvider
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutStatus
import com.hubtel.merchant.checkout.sdk.ux.pay.order.channelName
import com.hubtel.merchant.checkout.sdk.ux.pay.order.toPurchaseOrderItem
import com.hubtel.merchant.checkout.sdk.ux.pay.status.failed_transaction.FailedPaymentScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.status.order_placed.OrderPlacedScreen
import kotlinx.coroutines.delay

internal data class PaymentStatusScreen(
    private val providerName: String?,
    private val config: CheckoutConfig,
    private val checkoutType: CheckoutType?
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

        val screenHeight = LocalConfiguration.current.screenHeightDp
        val density = LocalDensity.current.density
        val maxHeight = (screenHeight / density).dp

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
                PaymentStatus.UNPAID, PaymentStatus.PAID -> orderStatus?.invoiceStatus

                else -> context.getString(R.string.checkout_payment_being_processed_msg)
            } ?: ""
        }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.checkout_processing_payment),
                    style = HubtelTheme.typography.body1
                )
            }, actions = {
                TextButton(onClick = {
                    checkoutActivity?.finishWithResult()
                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCancel)
                }) {
                    Text(
                        text = stringResource(id = R.string.checkout_cancel_order),
                        color = HubtelTheme.colors.error,
                    )
                }
            })
        }, bottomBar = {
            Column(
                Modifier
                    .padding(Dimens.paddingDefault)
                    .animateContentSize(),
            ) {

                when {
                    beforeInitialLoad -> {
                        LoadingTextButton(
                            text = stringResource(R.string.checkout_i_have_paid), onClick = {
                                paymentStatusViewModel.checkPaymentStatus(config)
                                recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapIHavePaid)
                            }, loading = isLoading, modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // TODO: Should be removed
                    paymentStatus == PaymentStatus.PAID -> {
                        Divider(
                            color = HubtelTheme.colors.outline,
                        )
                        LoadingTextButton(
                            text = stringResource(com.hubtel.core_ui.R.string.done), onClick = {
                                    checkoutActivity?.finishWithResult(orderStatus, paymentStatus)
//                                navigator?.push(
//                                    TransactionSuccessfulScreen(providerName = "", config)
//                                )
                                recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                            }, modifier = Modifier.fillMaxWidth()
                        )
                    }

                    paymentStatus == PaymentStatus.PENDING -> {
                        LoadingTextButton(
                            text = if (isCountingDown) {
                                val time = countDownMillis.formatTime()
                                "${stringResource(R.string.checkout_check_again)} ($time)"
                            } else stringResource(R.string.checkout_check_again), onClick = {
                                if (!isCountingDown) {
                                    countDownMillis = CHECK_COUNTDOWN_TIME
//                                        tapCount++

                                    paymentStatusViewModel.checkPaymentStatus(config)
                                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCheckAgain)
                                }
                            }, enabled = !isCountingDown, modifier = Modifier.fillMaxWidth()
                        )
                    }

                    paymentStatus == PaymentStatus.UNPAID -> {
                        LoadingTextButton(
                            text = if (isCountingDown) {
                                val time = countDownMillis.formatTime()
                                "${stringResource(R.string.checkout_check_again)} ($time)"
                            } else stringResource(R.string.checkout_check_again), onClick = {
                                if (!isCountingDown) {
                                    countDownMillis = CHECK_COUNTDOWN_TIME
//                                        tapCount++

                                    paymentStatusViewModel.checkPaymentStatus(config)
                                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCheckAgain)
                                }
                            }, enabled = !isCountingDown, modifier = Modifier.fillMaxWidth()
                        )
                    }

                    paymentStatus == PaymentStatus.EXPIRED -> {
                        LoadingTextButton(
                            text = stringResource(R.string.checkout_cancel_transaction_title),
                            onClick = {
                                checkoutActivity?.finishWithResult()
//                                    onPaymentComplete(false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    uiState.hasError -> {
                        LoadingTextButton(
                            text = if (isCountingDown) {
                                val time = countDownMillis.formatTime()
                                "${stringResource(R.string.checkout_check_again)} ($time)"
                            } else stringResource(R.string.checkout_check_again), onClick = {
                                if (!isCountingDown) {
                                    countDownMillis = CHECK_COUNTDOWN_TIME
//                                        tapCount++

                                    paymentStatusViewModel.checkPaymentStatus(config)
                                    recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCheckAgain)
                                }
                            }, enabled = !isCountingDown, modifier = Modifier.fillMaxWidth()
                        )
                    }

                    paymentStatus == PaymentStatus.FAILED -> {
                        LoadingTextButton(
                            text = stringResource(R.string.checkout_cancel_transaction_title),
                            onClick = {
                                checkoutActivity?.finishWithResult()
//                                    onPaymentComplete(false)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
//                    .padding(Dimens.paddingDefault),
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
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                }

                // check payment status request in flight
                if (isLoading) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(bottom = Dimens.paddingLarge)
                        )

                        Text(
                            text = stringResource(R.string.checkout_checking_status),
                            style = HubtelTheme.typography.h3,
                        )
                    }
                }

                /*                if (paymentStatus == PaymentStatus.PENDING && checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT) {
                                    Image(
                                        painter = painterResource(R.drawable.checkout_ic_pending),
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
                                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                                    )
                                }

                                if (paymentStatus == PaymentStatus.PENDING && checkoutType == CheckoutType.DIRECT_DEBIT) {
                                    Image(
                                        painter = painterResource(R.drawable.checkout_ic_pending),
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
                                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                                    )
                                }*/

                if (paymentStatus == PaymentStatus.PENDING && (checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT || checkoutType == CheckoutType.DIRECT_DEBIT || checkoutType == null)) {
                    Image(
                        painter = painterResource(R.drawable.checkout_ic_pending),
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
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                }

                if (uiState.hasError) {
                    Image(
                        painter = painterResource(R.drawable.checkout_ic_pending),
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
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                }


                if (paymentStatus == PaymentStatus.PAID && (checkoutType == CheckoutType.DIRECT_DEBIT || checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT)) {
//                    navigator?.push(PaidSuccessScreen())
                    PaidSuccessContent()
                }

                if (paymentStatus == PaymentStatus.PAID && checkoutType == CheckoutType.PRE_APPROVAL_CONFIRM) {

                    navigator?.push(
                        OrderPlacedScreen(
                            walletName = walletProvider?.name ?: "",
                            amount = orderStatus?.transactionAmount ?: 0.0
                        )
                    )
                }

                if (paymentStatus == PaymentStatus.PAID && checkoutType == null) {
                    PaidSuccessContent()
                }

                if (paymentStatus == PaymentStatus.UNPAID) {
                    Image(
                        painter = painterResource(R.drawable.checkout_ic_pending),
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
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                }

                if (paymentStatus == PaymentStatus.EXPIRED || paymentStatus == PaymentStatus.FAILED) {
                    navigator?.push(
                        FailedPaymentScreen(
                            providerName = providerName,
                            orderStatus?.mobileNumber,
                            config = config
                        )
                    )
                }

                if (walletProvider == WalletProvider.MTN && paymentStatus != PaymentStatus.PAID && !uiState.isLoading && checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT) {
                    MTNPromptApprovalGuide(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 50.dp,
                                start = Dimens.paddingDefault,
                                end = Dimens.paddingDefault
                            ),
                    )
                }
            }
        }


        if (showChangeWalletDialog) {
            CheckoutMessageDialog(
                onDismissRequest = { },
                painter = painterResource(com.hubtel.core_ui.R.drawable.core_ic_alert_red),
                message = stringResource(R.string.checkout_new_transactions),
                positiveText = stringResource(com.hubtel.core_ui.R.string.yes),
                negativeText = stringResource(com.hubtel.core_ui.R.string.core_cancel),
                onPositiveClick = {
                    showChangeWalletDialog = false
                    navigator?.pop()
                },
                onNegativeClick = { showChangeWalletDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = false, dismissOnClickOutside = false
                )
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
                            PurchaseFailedEvent(amount = orderStatus?.transactionAmount ?: 0.0,
                                errorMessage = errorMessage,
                                paymentType = walletProvider?.provider,
                                paymentChannel = walletProvider?.channelName,
                                purchaseOrderItems = orderItems,
                                purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                                purchaseOrderProviders = orderItems.map { it.provider ?: "" })
                        )
                    }

                    PaymentStatus.PAID -> {
                        recordCheckoutEvent(
                            CheckoutEvent.CheckoutPaymentSuccessfulViewPagePaymentSuccessful
                        )

                        val orderItems = listOf(config.toPurchaseOrderItem())

                        recordPurchaseEvent(
                            PurchaseEvent(orderId = orderStatus?.clientReference,
                                amount = orderStatus?.transactionAmount ?: 0.0,
                                paymentType = walletProvider?.provider,
                                paymentChannel = walletProvider?.channelName,
                                purchaseOrderItems = orderItems,
                                purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                                purchaseOrderProviders = orderItems.map { it.provider ?: "" })
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
    private fun SuccessfulTransaction(maxHeight: Dp, amountPaid: Double?) {
        val constraints = ConstraintSet {
            val backgroundBox = createRefFor("backgroundBox")
            val topBox = createRefFor("topBox")

            constrain(backgroundBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(topBox) {
                top.linkTo(backgroundBox.bottom, (-40).dp)
                start.linkTo(parent.start /* goneMargin = 15.dp*/)
                end.linkTo(parent.end /* goneMargin = 15.dp*/)
            }
        }

        ConstraintLayout(
            constraints, modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight)
                    .background(color = Color(0xFFDBF7E0))
                    .layoutId("backgroundBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkout_ic_success),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(Dimens.paddingNano)
                    )
                    Text(
                        text = stringResource(id = R.string.checkout_success),
                        style = HubtelTheme.typography.h3
                    )
                    val text = buildAnnotatedString {
                        append("GHS $amountPaid ")
                        pushStyle(style = SpanStyle(color = Color(0xFF359846)))
                        append(stringResource(R.string.checkout_paid_message))
                        pop()
                    }

                    Text(
                        text = text, style = HubtelTheme.typography.body1
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(Dimens.paddingDefault)
                    .background(Color(0xFFFFF4CC), shape = RoundedCornerShape(16.dp))
                    .layoutId("topBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_enterprise_insurance),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.checkout_orders_and_delivery),
                        style = HubtelTheme.typography.body1,
                        modifier = Modifier.padding(
                            bottom = Dimens.paddingDefault, top = Dimens.paddingNano
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun PaidSuccessContent() {
        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2) {
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
                        text = stringResource(id = R.string.checkout_success_message),
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )

                }
            }
        }
    }

    @Composable
    private fun UnsuccessfulTransaction(maxHeight: Dp, orderStatus: TransactionStatusInfo?) {
        val constraints = ConstraintSet {
            val backgroundBox = createRefFor("backgroundBox")
            val topBox = createRefFor("topBox")
            val buttonBox = createRefFor("buttonBox")

            constrain(backgroundBox) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            constrain(topBox) {
//                top.linkTo(backgroundBox.bottom, (-40).dp)
                top.linkTo(backgroundBox.bottom, -Dimens.paddingExtraLarge)
                start.linkTo(parent.start /* goneMargin = 15.dp*/)
                end.linkTo(parent.end /* goneMargin = 15.dp*/)
            }

            constrain(buttonBox) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(topBox.bottom, Dimens.paddingLarge)
            }
        }

        ConstraintLayout(
            constraints, modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxHeight)
                    .background(color = Color(0xFFFFABBB))
                    .layoutId("backgroundBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkout_ic_close_circle_red),
                        contentDescription = null,
                        modifier = Modifier
                            .size(64.dp)
                            .padding(Dimens.paddingNano)
                    )
                    Text(
                        text = stringResource(id = R.string.checkout_failed),
                        style = HubtelTheme.typography.h3,
                        modifier = Modifier.padding(bottom = Dimens.paddingDefault)
                    )

                    Text(
                        text = stringResource(id = R.string.checkout_failed),
                        style = HubtelTheme.typography.body1
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.paddingDefault)
                    .background(Color(0xFFFFF4CC), shape = RoundedCornerShape(10.dp))
                    .layoutId("topBox")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(Dimens.paddingDefault)
                ) {
//                    Surface(
//                        shape = CircleShape,
//                        color = Color.White,
//                        modifier = Modifier.padding(Dimens.paddingNano)
//                    ) {
//                        Image(
//                            painter = painterResource(id = R.drawable.checkout_mtn_momo),
//                            contentDescription = null,
//                            modifier = Modifier.size(30.dp)
//                        )
//                    }
                    Text(
                        text = providerName ?: "",
                        style = HubtelTheme.typography.body1,
                        modifier = Modifier.padding(
                            bottom = Dimens.paddingNano,
                            top = Dimens.paddingNano,
                        )
                    )
                    Text(
                        text = orderStatus?.mobileNumber ?: "", style = HubtelTheme.typography.body1
                    )
                }
            }

            Box(contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(Dimens.paddingDefault)
//                    .background(color = Color.Blue)
                    .clickable {
                        // TODO: implement
                    }
                    .layoutId("buttonBox")) {
                Column {
                    Divider(
                        color = HubtelTheme.colors.outline,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Change Wallet", modifier = Modifier
                                .padding(
                                    top = Dimens.paddingDefault, bottom = Dimens.paddingDefault
                                )
                                .weight(2f)
                        )
                        Image(
                            painter = painterResource(id = R.drawable.checkout_ic_forward_arrow),
                            contentDescription = null,
                        )
                    }
                    Divider(
                        color = HubtelTheme.colors.outline,
                    )
                }
            }
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
    paymentStatus: PaymentStatus = com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentStatus.UNPAID,
) {
    submitCheckoutResult(
        CheckoutStatus(
            transactionId = orderStatus?.transactionId,
            isCanceled = orderStatus == null,
            isPaymentSuccessful = paymentStatus == com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentStatus.PAID
        )
    )
}

