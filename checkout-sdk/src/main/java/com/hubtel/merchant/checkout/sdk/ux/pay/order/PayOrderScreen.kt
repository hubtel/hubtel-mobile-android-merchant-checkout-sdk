package com.hubtel.merchant.checkout.sdk.ux.pay.order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.core_utils.extensions.formatMoney
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.BeginPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordBeginPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CARD_SETUP
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CHECKOUT
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CHECKOUT_SUCCESS_DIALOG
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.COLLECT_DEVICE_INFO
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.GET_FEES
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.PAYMENT_COMPLETED
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.PAY_ORDER
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.VERIFY_CARD
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.BANK_CARD
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.MOBILE_MONEY
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ExpandableBankCardOption
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ExpandableMomoOption
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusScreen
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.validate_3ds.VerificationDialog3ds

internal data class PayOrderScreen(
    private val config: CheckoutConfig,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel = viewModel<PayOrderViewModel>(
            factory = PayOrderViewModel.getViewModelFactory(config.apiKey),
        )

        PayOrderScreenContent(viewModel)
    }

    @Composable
    private fun PayOrderScreenContent(
        viewModel: PayOrderViewModel,
    ) {
        val bankWallets = viewModel.bankWallets
        val paymentInfo = viewModel.paymentInfo
        val orderTotal = viewModel.orderTotal

        val checkoutFeesUiState by viewModel.checkoutFeesUiState
        val cardSetupUiState by viewModel.threeDSSetupUiState
        val checkoutUiState by viewModel.checkoutUiState

        val paymentChannelsUiState by viewModel.paymentChannelsUiState
        val bankChannels = viewModel.bankChannels
        val momoChannels = viewModel.momoChannels

        var showCancelDialog by remember { mutableStateOf(false) }

        val walletUiState = remember { PaymentWalletUiState(null) }
        var currentCheckoutStep: CheckoutStep by remember { mutableStateOf(GET_FEES) }

        val bankCardUiState = remember(bankWallets) {
            BankCardUiState(
                wallet = bankWallets.firstOrNull(),
                useSavedBankCard = bankWallets.isNotEmpty()
            )
        }

        val momoWalletUiState = remember { MomoWalletUiState() }

        val feeItems = remember(checkoutFeesUiState) {
            checkoutFeesUiState.data ?: emptyList()
        }

        val shouldShowWebView = remember(
            walletUiState,
            currentCheckoutStep,
        ) {
            currentCheckoutStep in COLLECT_DEVICE_INFO..VERIFY_CARD
                    && walletUiState.isBankCard
        }

        var isPayButtonEnabled by remember { mutableStateOf(false) }

        val isLoading by remember {
            derivedStateOf {
                (cardSetupUiState.isLoading
                        || checkoutUiState.isLoading
                        || currentCheckoutStep == COLLECT_DEVICE_INFO)
                        && currentCheckoutStep in CARD_SETUP..CHECKOUT
            }
        }

        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        HBScaffold(
            topBar = {
                HBTopAppBar {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(Modifier.size(48.dp))

                        IconButton(
                            onClick = {
                                showCancelDialog = true
                                recordCheckoutEvent(CheckoutEvent.CheckoutPayTapClose)
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.checkout_ic_close_circle),
                                contentDescription = stringResource(R.string.checkout_close),
                            )
                        }
                    }
                }
            },
            bottomBar = {
                Column(Modifier.animateContentSize()) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        thickness = 2.dp,
                    )

                    LoadingTextButton(
                        text = "${stringResource(R.string.checkout_pay)} ${orderTotal.formatMoney()}",
                        onClick = {
                            currentCheckoutStep = PAY_ORDER
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapButtonPay)
                        },
                        enabled = isPayButtonEnabled,
                        loading = checkoutFeesUiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(Dimens.paddingDefault),
                    )
                }
            },
            backgroundColor = HubtelTheme.colors.uiBackground,
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {

                CheckoutReceiptCard(
                    fees = feeItems,
                    amount = config.amount,
                    total = orderTotal,
                    modifier = Modifier
                        .padding(Dimens.paddingMedium)
                        .animateContentSize()
                )


                Text(
                    text = stringResource(R.string.checkout_pay_with),
                    style = HubtelTheme.typography.h2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.paddingDefault)
                        .padding(horizontal = Dimens.paddingDefault)
                        .background(
                            color = HubtelTheme.colors.cardBackground,
                            shape = HubtelTheme.shapes.medium.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp),
                            ),
                        )
                        .padding(Dimens.paddingDefault),
                )

                if (paymentChannelsUiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.paddingDefault)
                            .background(HubtelTheme.colors.uiBackground2)
                            .padding(Dimens.paddingDefault)
                    ) {
                        CircularProgressIndicator(
                            color = CheckoutTheme.colors.colorPrimary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                AnimatedVisibility(momoChannels.isNotEmpty()) {
                    // Mobile Money
                    ExpandableMomoOption(
                        state = momoWalletUiState,
                        channels = momoChannels,
                        expanded = walletUiState.isMomoWallet,
                        onExpand = {
                            walletUiState.setWalletType(MOBILE_MONEY)
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
                        },
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                    )
                }

                if (bankChannels.isNotEmpty()) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault * 2),
                    )
                }

                AnimatedVisibility(bankChannels.isNotEmpty()) {
                    // Bank Card
                    ExpandableBankCardOption(
                        state = bankCardUiState,
                        channels = bankChannels,
                        wallets = bankWallets,
                        expanded = walletUiState.isBankCard,
                        onExpand = {
                            walletUiState.setWalletType(BANK_CARD)
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapBankCard)
                        },
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.paddingDefault)
                        .background(
                            color = HubtelTheme.colors.cardBackground,
                            shape = HubtelTheme.shapes.medium.copy(
                                topStart = CornerSize(0.dp),
                                topEnd = CornerSize(0.dp),
                            )
                        )
                        .padding(top = Dimens.paddingDefault),
                )

                Spacer(modifier = Modifier.height(50.dp))
            }
        }

        if (isLoading) {
            HBProgressDialog(
                message = "${stringResource(R.string.checkout_please_wait)}...",
                progressColor = CheckoutTheme.colors.colorPrimary,
            )
        }

        if (shouldShowWebView) {
            VerificationDialog3ds(
                step = currentCheckoutStep,
                setupState = remember(cardSetupUiState) {
                    val threeDSSetupInfo = cardSetupUiState.data

                    ThreeDSSetupState(
                        accessToken = threeDSSetupInfo?.accessToken,
                        referenceId = threeDSSetupInfo?.clientReference
                    )
                },
                verificationState = remember(checkoutUiState) {
                    val checkoutInfo = checkoutUiState.data

                    Verification3dsState(
                        jwt = checkoutInfo?.jwt,
                        customData = checkoutInfo?.customData,
                    )
                },
                onCollectionComplete = { currentCheckoutStep = CHECKOUT },
                onCardVerified = { currentCheckoutStep = PAYMENT_COMPLETED },
                onBackClick = {
                    if (currentCheckoutStep == VERIFY_CARD) {
                        showCancelDialog = true
                    }
                },
            )
        }

        if (showCancelDialog) {
            CheckoutMessageDialog(
                onDismissRequest = { showCancelDialog = false },
                titleText = stringResource(R.string.checkout_cancel_transaction_title),
                message = stringResource(R.string.checkout_cancel_transaction_msg),
                positiveText = stringResource(R.string.checkout_no),
                negativeText = stringResource(R.string.checkout_yes),
                onPositiveClick = { showCancelDialog = false },
                onNegativeClick = { activity.finish() },
            )
        }

        if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG) {
            CheckoutMessageDialog(
                onDismissRequest = {},
                titleText = stringResource(R.string.checkout_success),
                message = stringResource(
                    R.string.checkout_momo_bill_prompt_msg,
                    paymentInfo?.accountNumber ?: "",
                ),
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = { currentCheckoutStep = PAYMENT_COMPLETED },
                properties = DialogProperties(
                    dismissOnBackPress = false, dismissOnClickOutside = false
                )
            )
        }

        if (currentCheckoutStep == CARD_SETUP && cardSetupUiState.hasError) {
            CheckoutMessageDialog(
                onDismissRequest = { currentCheckoutStep = GET_FEES },
                titleText = stringResource(R.string.checkout_card_not_supported),
                message = cardSetupUiState.error?.asString()
                    ?: stringResource(R.string.checkout_card_not_supported_msg),
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = { currentCheckoutStep = GET_FEES },
            )
        }

        if (checkoutUiState.hasError && currentCheckoutStep == CHECKOUT) {
            CheckoutMessageDialog(
                onDismissRequest = { currentCheckoutStep = GET_FEES },
                painter = painterResource(R.drawable.checkout_ic_alert_red),
                message = checkoutUiState.error?.asString() ?: "",
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = { currentCheckoutStep = GET_FEES },
            )
        }

        LaunchedEffect(Unit) {
            viewModel.getPaymentChannels(config.posSalesId)
            viewModel.initData(config.amount)
            recordCheckoutEvent(CheckoutEvent.CheckoutPayViewPagePay)
        }

        LaunchedEffect(
            walletUiState.payOrderWalletType,
            bankCardUiState.useSavedBankCard,
            bankCardUiState.saveForLater,
            bankCardUiState.selectedWallet,
            bankCardUiState.cardHolderName,
            bankCardUiState.cardNumber,
            bankCardUiState.monthYear,
            bankCardUiState.cvv,
            momoWalletUiState.walletProvider,
            momoWalletUiState.mobileNumber,
        ) {
            // update payment info object when user checkout input
            // changes
            val walletType = walletUiState.payOrderWalletType ?: return@LaunchedEffect

            isPayButtonEnabled = when (walletType) {
                MOBILE_MONEY -> momoWalletUiState.isValid
                BANK_CARD -> bankCardUiState.isValid
            }

            viewModel.updatePaymentInfo(
                walletType,
                momoWalletUiState,
                bankCardUiState
            )

            // ignore state change if we're not in the get
            // fees state or user wallet is hubtel balance
            if (currentCheckoutStep != GET_FEES) return@LaunchedEffect

            if (
                (walletUiState.isBankCard && !bankCardUiState.isValid)
                || (walletUiState.isMomoWallet && !momoWalletUiState.isValid)
            ) return@LaunchedEffect

            viewModel.getCheckoutFees(config)
        }

        LaunchedEffect(
            cardSetupUiState,
            checkoutUiState,
        ) {
            val walletType = walletUiState.payOrderWalletType ?: return@LaunchedEffect

            val nextStep = when (currentCheckoutStep) {
                CARD_SETUP -> getNextStepAfterCardSetup(cardSetupUiState)
//            CARD_ENROLLMENT -> getNextStepAfterCardEnrollment(cardEnrollmentUiState)
                CHECKOUT -> getNextStepAfterCheckout(walletType, checkoutUiState)
                else -> null
            }

            if (nextStep != null) {
                currentCheckoutStep = nextStep
            }
        }

        LaunchedEffect(currentCheckoutStep) {
            when (currentCheckoutStep) {
                PAY_ORDER -> {
                    walletUiState.payOrderWalletType?.let { walletType ->
                        currentCheckoutStep =
                            getNextStepAfterPayOrder(walletType, checkoutFeesUiState)
                    }
                }

                CARD_SETUP -> {
                    viewModel.validateCardDetails(config)
                }

                CHECKOUT -> {
                    walletUiState.payOrderWalletType?.let { walletType ->
                        viewModel.payOrder(config, walletType)
                    }
                }

                PAYMENT_COMPLETED -> {
                    navigator?.push(
                        PaymentStatusScreen(
                            providerName = paymentInfo?.providerName,
                            config = config
                        )
                    )

                    return@LaunchedEffect
                }

                else -> {}
            }
        }

        LaunchedEffect(Unit) {
            val orderItems = listOf(config.toPurchaseOrderItem())

            recordBeginPurchaseEvent(
                BeginPurchaseEvent(
                    amount = config.amount,
                    purchaseOrderItems = orderItems,
                    purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                    purchaseOrderProviders = orderItems.map { it.provider ?: "" }
                )
            )
        }

        BackHandler {
            when (currentCheckoutStep) {
                CHECKOUT_SUCCESS_DIALOG,
                in CARD_SETUP..VERIFY_CARD -> {
                }

                else -> {
                    showCancelDialog = true
                }
            }
        }
    }


    private fun getNextStepAfterPayOrder(
        walletType: PayOrderWalletType,
        checkoutFeeUiState: UiState2<List<CheckoutFee>>
    ): CheckoutStep {
        val hasFees = checkoutFeeUiState.success
                && !checkoutFeeUiState.isLoading

        val nextStep = when (walletType) {
            MOBILE_MONEY -> if (hasFees) CHECKOUT else null
            BANK_CARD -> if (hasFees) CARD_SETUP else null
        }

        // if next is null go back down to get fees
        return nextStep ?: GET_FEES
    }

    private fun getNextStepAfterCardSetup(
        cardSetupUiState2: UiState2<ThreeDSSetupInfo>
    ): CheckoutStep {
        return if (cardSetupUiState2.success
            && cardSetupUiState2.hasData &&
            !cardSetupUiState2.isLoading
        ) {
            COLLECT_DEVICE_INFO
        } else CARD_SETUP
    }

    private fun getNextStepAfterCheckout(
        walletType: PayOrderWalletType,
        checkoutUiState: UiState2<CheckoutInfo>
    ): CheckoutStep {
        return if (checkoutUiState.success
            && checkoutUiState.hasData
            && !checkoutUiState.isLoading
        ) {
            when (walletType) {
                BANK_CARD -> VERIFY_CARD
                MOBILE_MONEY -> CHECKOUT_SUCCESS_DIALOG
            }
        } else CHECKOUT
    }
}