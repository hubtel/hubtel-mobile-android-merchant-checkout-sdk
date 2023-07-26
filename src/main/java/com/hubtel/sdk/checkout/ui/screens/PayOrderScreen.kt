package com.hubtel.feature_checkout.ui.pay_order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.hubtel.core_analytics.events.sections.CheckoutEvent
import com.hubtel.core_analytics.recordCheckoutEvent
import com.hubtel.core_network.model.response.CheckoutInfo
import com.hubtel.core_storage.model.checkout.CheckoutFee
import com.hubtel.core_ui.components.custom.HBMessageDialog
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.core_utils.extensions.formatMoney
import com.hubtel.feature_checkout.R
import com.hubtel.sdk.checkout.ux.pay.order.CheckoutStep.*
import com.hubtel.sdk.checkout.ux.pay.order.PayOrderWalletType.*
import com.hubtel.sdk.checkout.R
import com.hubtel.sdk.checkout.ui.components.ExpandableBankCardOption
import com.hubtel.sdk.checkout.ui.components.LoadingTealTextButton
import com.hubtel.sdk.checkout.ui.components.VerificationDialog3ds
import com.hubtel.sdk.checkout.ui.screens.BankCardUiState
import com.hubtel.sdk.checkout.ui.screens.CheckoutStep
import com.hubtel.sdk.checkout.ui.screens.MomoWalletUiState
import com.hubtel.sdk.checkout.ui.screens.PayOrderViewModel
import com.hubtel.sdk.checkout.ui.screens.PayOrderWalletType
import com.hubtel.sdk.checkout.ui.screens.PaymentWalletUiState
import com.hubtel.sdk.checkout.ux.pay.order.CheckoutReceiptCard
import com.hubtel.sdk.checkout.ux.pay.order.ThreeDSSetupState
import com.hubtel.sdk.checkout.ux.pay.order.Verfication3dsState
import timber.log.Timber


@Composable
 fun PayOrderScreen(
    viewModel: PayOrderViewModel
) {

    val momoWallets = viewModel.momoWallets
    val bankWallets = viewModel.bankWallets
    val hubtelWallet = viewModel.hubtelWallet
    val paymentInfo = viewModel.paymentInfo
    val orderTotal = viewModel.orderTotal
    val checkoutFeesUiState by viewModel.checkoutFeesUiState
    val cardSetupUiState by viewModel.threeDSSetupUiState
    val checkoutUiState by viewModel.checkoutUiState

    var showCancelDialog by remember { mutableStateOf(false) }

    val walletUiState = remember { PaymentWalletUiState() }
    var currentCheckoutStep: CheckoutStep by remember { mutableStateOf(CheckoutStep.GET_FEES) }

    val momoWalletUiState = remember(momoWallets) {
        MomoWalletUiState(
            wallet = momoWallets.firstOrNull()
        )
    }

    val bankCardUiState = remember(bankWallets) {
        BankCardUiState(
            wallet = bankWallets.firstOrNull(),
            useSavedBankCard = bankWallets.isNotEmpty()
        )
    }

    val feeItems = remember(checkoutFeesUiState) {
        checkoutFeesUiState.data ?: emptyList()
    }

    val shouldShowWebView = remember(
        walletUiState,
        currentCheckoutStep,
    ) {
        walletUiState.isBankCard && currentCheckoutStep in CheckoutStep.COLLECT_DEVICE_INFO..CheckoutStep.VERIFY_CARD
    }

    var isPayButtonEnabled by remember { mutableStateOf(false) }

    val isLoading by remember {
        derivedStateOf {
            (cardSetupUiState.isLoading
                    || checkoutUiState.isLoading
                    || currentCheckoutStep == CheckoutStep.COLLECT_DEVICE_INFO)
                    && currentCheckoutStep in CheckoutStep.CARD_SETUP..CheckoutStep.CHECKOUT
        }
    }

    HBScaffold(
        topBar = {
            Box(Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        showCancelDialog = true
                        recordCheckoutEvent(CheckoutEvent.CheckoutPayTapClose)
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.checkout_ic_close_circle),
                        contentDescription = stringResource(R.string.close),
                    )
                }
            }
        },
        bottomBar = {
            Column(Modifier.animateContentSize()) {
                Divider(
                    color = HubtelTheme.colors.outline,
                    thickness = 2.dp,
                )

                LoadingTealTextButton(
                    text = "${stringResource(R.string.fc_pay)} ${orderTotal.formatMoney()}",
                    onClick = {
                        currentCheckoutStep = CheckoutStep.PAY_ORDER
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
                logoUrl = "https://i0.wp.com/thebftonline.com/wp-content/uploads/2021/08/vodafone-cash-ghana-min.jpg?fit=1200%2C1200&ssl=1",
                accountName = "David Glover",
                accountNumber = "Vodafone Cash",
                serviceName = "Vodafone Cash",
                amount = 20.00,
                total = 40.00,
                modifier = Modifier
                    .padding(Dimens.paddingMedium)
                    .animateContentSize()
            )


            Text(
                text = stringResource(com.hubtel.feature_checkout.R.string.fc_pay_with),
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


//            // Mobile Money
//            ExpandableMomoOption(
//                state = momoWalletUiState,
//                wallets = viewModel.momoWallets,
//                expanded = walletUiState.isMomoWallet,
//                onExpand = {
//                    walletUiState.setWalletType(MOBILE_MONEY)
//                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
//                },
//                onAddNewNumberClick = { viewModel.addMomoWallet() },
//                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
//            )


            // Bank Card
            ExpandableBankCardOption(
                state = bankCardUiState,
                wallets = bankWallets,
                expanded = walletUiState.isBankCard,
                onExpand = {
                    walletUiState.setWalletType(PayOrderWalletType.BANK_CARD)
//                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapBankCard)
                },
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            )


//            ExpandableHubtelBalanceOption(
//                wallet = hubtelWallet,
//                expanded = walletUiState.isHubtelBalance,
//                onExpand = {
//                    walletUiState.setWalletType(HUBTEL_BALANCE)
//                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapHubtelBalance)
//                },
//                modifier = Modifier.padding(horizontal = Dimens.paddingDefault)
//            )


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
        }
    }

    if (isLoading) {
        HBProgressDialog(
            message = "${stringResource(R.string.fc_please_wait)}..."
        )
    }

    if (shouldShowWebView) {
        VerificationDialog3ds(
            step = currentCheckoutStep,
            setupState = remember(cardSetupUiState) {
                val threeDSSetupInfo = cardSetupUiState.data

                ThreeDSSetupState(
                    accessToken = threeDSSetupInfo?.accessToken,
                    referenceId = threeDSSetupInfo?.referenceId
                )
            },
            verificationState = remember(checkoutUiState) {
                val checkoutInfo = checkoutUiState.data

                Verfication3dsState(
                    jwt = checkoutInfo?.cardStepUpJwt,
                    customData = checkoutInfo?.customCardData
                )
            },
            onCollectionComplete = { currentCheckoutStep = CHECKOUT },
            onCardVerified = { currentCheckoutStep = PAYMENT_STATUS },
            onBackClick = {
                if (currentCheckoutStep == CheckoutStep.VERIFY_CARD) {
                    showCancelDialog = true
                }
            },
        )
    }

    if (showCancelDialog) {
        HBMessageDialog(
            onDismissRequest = { showCancelDialog = false },
            titleText = stringResource(R.string.fc_cancel_transaction_title),
            message = stringResource(R.string.fc_cancel_transaction_msg),
            positiveText = stringResource(R.string.no),
            negativeText = stringResource(R.string.yes),
            onPositiveClick = { showCancelDialog = false },
            onNegativeClick = { onFinish() },
            flipButtonPositions = true,
        )
    }



    if (currentCheckoutStep == CheckoutStep.CHECKOUT_SUCCESS_DIALOG) {
        HBMessageDialog(
            onDismissRequest = {},
            titleText = stringResource(R.string.fc_success),
            message = stringResource(
                R.string.fc_momo_bill_prompt_msg,
                paymentInfo?.accountNumber ?: "",
            ),
            positiveText = stringResource(R.string.core_okay),
            onPositiveClick = { currentCheckoutStep = CheckoutStep.PAYMENT_STATUS },
            properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            )
        )
    }

    if (currentCheckoutStep == CheckoutStep.CARD_SETUP && cardSetupUiState.hasError) {
        HBMessageDialog(
            onDismissRequest = { currentCheckoutStep = CheckoutStep.GET_FEES },
            titleText = stringResource(R.string.checkout_card_not_supported),
            message = cardSetupUiState.error?.asString()
                ?: stringResource(R.string.checkout_card_not_supported_msg),
            positiveText = stringResource(R.string.checkout_okay),
            onPositiveClick = { currentCheckoutStep = CheckoutStep.GET_FEES },
        )
    }

    if (checkoutUiState.hasError && currentCheckoutStep == CheckoutStep.CHECKOUT) {
        HBMessageDialog(
            onDismissRequest = { currentCheckoutStep = CheckoutStep.GET_FEES },
            painter = painterResource(R.drawable.core_ic_alert_red),
            message = checkoutUiState.error?.asString() ?: "",
            positiveText = stringResource(R.string.checkout_okay),
            onPositiveClick = { currentCheckoutStep = CheckoutStep.GET_FEES },
        )
    }

    LaunchedEffect(Unit) {
        viewModel.initData(config.amount)
        recordCheckoutEvent(CheckoutEvent.CheckoutPayViewPagePay)
    }

    LaunchedEffect(
        walletUiState.payOrderWalletType,
        momoWalletUiState.selectedWallet,
        momoWalletUiState.walletProvider,
        bankCardUiState.useSavedBankCard,
        bankCardUiState.saveForLater,
        bankCardUiState.selectedWallet,
        bankCardUiState.cardNumber,
        bankCardUiState.monthYear,
        bankCardUiState.cvv,
    ) {

        // update payment info object when user checkout input
        // changes
        walletUiState.payOrderWalletType?.let { walletType ->
            isPayButtonEnabled = when (walletType) {
                PayOrderWalletType.MOBILE_MONEY -> momoWalletUiState.isValid
                PayOrderWalletType.BANK_CARD -> bankCardUiState.isValid
                PayOrderWalletType.HUBTEL_BALANCE -> true
            }


            viewModel.updatePaymentInfo(
                walletType, momoWalletUiState, bankCardUiState, hubtelWallet
            )
        }

        // ignore state change if we're not in the get
        // fees state or user wallet is hubtel balance
        if (currentCheckoutStep != CheckoutStep.GET_FEES) return@LaunchedEffect

        if (walletUiState.isBankCard && !bankCardUiState.isValid) return@LaunchedEffect

        viewModel.getCheckoutFees(config)
    }

    LaunchedEffect(
        cardSetupUiState,
        checkoutUiState,
    ) {
        val walletType = walletUiState.payOrderWalletType ?: return@LaunchedEffect

        val nextStep = when (currentCheckoutStep) {
            CheckoutStep.CARD_SETUP -> getNextStepAfterCardSetup(cardSetupUiState)
//            CARD_ENROLLMENT -> getNextStepAfterCardEnrollment(cardEnrollmentUiState)
            CheckoutStep.CHECKOUT -> getNextStepAfterCheckout(walletType, checkoutUiState)
            else -> null
        }

        if (nextStep != null) {
            currentCheckoutStep = nextStep
        }
    }

    LaunchedEffect(currentCheckoutStep) {
        when (currentCheckoutStep) {
            CheckoutStep.PAY_ORDER -> {
                walletUiState.payOrderWalletType?.let { walletType ->
                    currentCheckoutStep = getNextStepAfterPayOrder(walletType, checkoutFeesUiState)
                }
            }

            CheckoutStep.CARD_SETUP -> {
                viewModel.validateCardDetails(config)
            }

            CheckoutStep.CHECKOUT -> {
                viewModel.payOrder(config)
            }

            CheckoutStep.PAYMENT_STATUS -> {
                val orderId = checkoutUiState.data?.order?.id ?: ""
                val providerName = viewModel.paymentInfo?.providerName

                Timber.tag(TAG).i(orderId)
                navigator.gotoCheckPaymentStatus(orderId, providerName)

                return@LaunchedEffect
            }

            else -> {}
        }
    }

    BackHandler {
        when (currentCheckoutStep) {
            CheckoutStep.CHECKOUT_SUCCESS_DIALOG,
            in CheckoutStep.PIN_PROMPT..CheckoutStep.VERIFY_CARD -> {
            }

            else -> {
                showCancelDialog = true
            }
        }
    }

    // launched effect for tracking analytic events
    LaunchedEffect(
        currentCheckoutStep,
        checkoutUiState
    ) {
        // analytics record for failed checkout order
        if (checkoutUiState.hasError && currentCheckoutStep == CheckoutStep.CHECKOUT) {
            recordCheckoutEvent(CheckoutEvent.CheckoutPayViewDialogOrderFailed)
        }

        // analytic event for order success dialog displayed
        if (currentCheckoutStep == CheckoutStep.CHECKOUT_SUCCESS_DIALOG) {
            recordCheckoutEvent(CheckoutEvent.CheckoutPayViewDialogOrderCreatedSuccessfully)
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
        PayOrderWalletType.MOBILE_MONEY -> if (hasFees) CheckoutStep.CHECKOUT else null
        PayOrderWalletType.BANK_CARD -> if (hasFees) CheckoutStep.CARD_SETUP else null
        PayOrderWalletType.HUBTEL_BALANCE -> CheckoutStep.PIN_PROMPT
    }

    // if next is null go back down to get fees
    return nextStep ?: CheckoutStep.GET_FEES
}

private fun getNextStepAfterCardSetup(
    cardSetupUiState2: UiState2<CheckoutInfo>
): CheckoutStep {
    return if (cardSetupUiState2.success
        && cardSetupUiState2.hasData &&
        !cardSetupUiState2.isLoading
    ) {
        CheckoutStep.COLLECT_DEVICE_INFO
    } else CheckoutStep.CARD_SETUP
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
            BANK_CARD -> CheckoutStep.VERIFY_CARD
            MOBILE_MONEY -> CHECKOUT_SUCCESS_DIALOG
            HUBTEL_BALANCE -> PAYMENT_STATUS
        }
    } else CHECKOUT
}

private const val TAG = "PayOrderScreen"