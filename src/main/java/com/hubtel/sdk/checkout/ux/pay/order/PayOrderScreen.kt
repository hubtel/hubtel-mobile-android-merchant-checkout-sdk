package com.hubtel.sdk.checkout.ux.pay.order

//import com.hubtel.core_analytics.events.sections.CheckoutEvent
//import com.hubtel.core_analytics.recordCheckoutEvent
//import com.hubtel.core_storage.model.checkout.CheckoutFee
//import com.hubtel.core_storage.model.checkout.CheckoutInfo
//import com.hubtel.core_storage.model.checkout.ThreeDSSetupInfo
//import com.hubtel.feature_auth.ui.auth_pin.AuthPinDialog
//import com.hubtel.feature_checkout.R
//import com.hubtel.feature_checkout.model.CheckoutConfig
//import com.hubtel.feature_checkout.navigator.CheckoutNavigator
//import com.hubtel.feature_checkout.util.destination
//import com.hubtel.feature_checkout.util.serviceName
//import com.hubtel.sdk.checkout.ux.pay_order.PayOrderViewModel
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
import com.hubtel.core_network.model.response.CheckoutInfo
import com.hubtel.core_network.model.response.ThreeDSSetupInfo
import com.hubtel.core_ui.components.custom.HBMessageDialog
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.feature_checkout.ui.validate_3ds.VerificationDialog3ds
import com.hubtel.sdk.checkout.R
import com.hubtel.sdk.checkout.ux.components.LoadingTealTextButton
import com.hubtel.sdk.checkout.ux.pay.order.CheckoutStep.*
import com.hubtel.sdk.checkout.ux.pay.order.PayOrderWalletType.*
import com.hubtel.sdk.checkout.ux.pay.order.components.ExpandableBankCardOption
import com.hubtel.sdk.checkout.ux.pay.order.components.ExpandableMomoOption
import timber.log.Timber


@Composable
internal fun PayOrderScreen(viewModel: PayOrderViewModel) {

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
    var currentCheckoutStep: CheckoutStep by remember { mutableStateOf(GET_FEES) }

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
        walletUiState.isBankCard && currentCheckoutStep in COLLECT_DEVICE_INFO..VERIFY_CARD
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

    HBScaffold(
        topBar = {
            Box(Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = {
                        showCancelDialog = true
//                        recordCheckoutEvent(CheckoutEvent.CheckoutPayTapClose)
                    },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.checkout_ic_close_circle),
                        contentDescription = stringResource(R.string.checkout_close),
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
                    text = "${stringResource(R.string.checkout_pay)} ${orderTotal.formatMoney()}",
                    onClick = {
                        currentCheckoutStep = PAY_ORDER
//                        recordCheckoutEvent(CheckoutEvent.CheckoutPayTapButtonPay)
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
                accountNumber = "050 *** 2027",
                serviceName = "Vodafone Cash",
                fees = feeItems,
                amount = 20.00,
                total = 40.00,
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


            // Mobile Money
            ExpandableMomoOption(
                state = momoWalletUiState,
                wallets = viewModel.momoWallets,
                expanded = walletUiState.isMomoWallet,
                onExpand = {
                    walletUiState.setWalletType(MOBILE_MONEY)
//                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
                },
                onAddNewNumberClick = { viewModel.addMomoWallet() },
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            )


            // Bank Card
            ExpandableBankCardOption(
                state = bankCardUiState,
                wallets = bankWallets,
                expanded = walletUiState.isBankCard,
                onExpand = {
                    walletUiState.setWalletType(BANK_CARD)
//                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapBankCard)
                },
                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
            )


//            ExpandableHubtelBalanceOption(
//                wallet = hubtelWallet,
//                expanded = walletUiState.isHubtelBalance,
//                onExpand = {
//                    walletUiState.setWalletType(HUBTEL_BALANCE)
////                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapHubtelBalance)
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
            message = "${stringResource(R.string.checkout_please_wait)}..."
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
                if (currentCheckoutStep == VERIFY_CARD) {
                    showCancelDialog = true
                }
            },
        )
    }

    if (showCancelDialog) {
        HBMessageDialog(
            onDismissRequest = { showCancelDialog = false },
            titleText = stringResource(R.string.checkout_cancel_transaction_title),
            message = stringResource(R.string.checkout_cancel_transaction_msg),
            positiveText = stringResource(R.string.checkout_no),
            negativeText = stringResource(R.string.checkout_yes),
            onPositiveClick = { showCancelDialog = false },
            onNegativeClick = { onFinish() },
        )
    }

    if (currentCheckoutStep == PIN_PROMPT) {
        AuthPinDialog(
            onDismiss = { currentCheckoutStep = GET_FEES },
            onPinVerified = { currentCheckoutStep = CHECKOUT },
        )
    }

    if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG) {
        HBMessageDialog(
            onDismissRequest = {},
            titleText = stringResource(R.string.checkout_success),
            message = stringResource(
                R.string.checkout_momo_bill_prompt_msg,
                paymentInfo?.accountNumber ?: "",
            ),
            positiveText = stringResource(R.string.checkout_okay),
            onPositiveClick = { currentCheckoutStep = PAYMENT_STATUS },
            properties = DialogProperties(
                dismissOnBackPress = false, dismissOnClickOutside = false
            )
        )
    }

    if (currentCheckoutStep == CARD_SETUP && cardSetupUiState.hasError) {
        HBMessageDialog(
            onDismissRequest = { currentCheckoutStep = GET_FEES },
            titleText = stringResource(R.string.checkout_card_not_supported),
            message = cardSetupUiState.error?.asString()
                ?: stringResource(R.string.checkout_card_not_supported_msg),
            positiveText = stringResource(R.string.checkout_okay),
            onPositiveClick = { currentCheckoutStep = GET_FEES },
        )
    }

    if (checkoutUiState.hasError && currentCheckoutStep == CHECKOUT) {
        HBMessageDialog(
            onDismissRequest = { currentCheckoutStep = GET_FEES },
            painter = painterResource(R.drawable.checkout_ic_alert_red),
            message = checkoutUiState.error?.asString() ?: "",
            positiveText = stringResource(R.string.checkout_okay),
            onPositiveClick = { currentCheckoutStep = GET_FEES },
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
                MOBILE_MONEY -> momoWalletUiState.isValid
                BANK_CARD -> bankCardUiState.isValid
                HUBTEL_BALANCE -> true
            }


            viewModel.updatePaymentInfo(
                walletType, momoWalletUiState, bankCardUiState, hubtelWallet
            )
        }

        // ignore state change if we're not in the get
        // fees state or user wallet is hubtel balance
        if (currentCheckoutStep != GET_FEES) return@LaunchedEffect

        if (walletUiState.isBankCard && !bankCardUiState.isValid) return@LaunchedEffect

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
                    currentCheckoutStep = getNextStepAfterPayOrder(walletType, checkoutFeesUiState)
                }
            }

            CARD_SETUP -> {
                viewModel.validateCardDetails(config)
            }

            CHECKOUT -> {
                viewModel.payOrder(config)
            }

            PAYMENT_STATUS -> {
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
            CHECKOUT_SUCCESS_DIALOG,
            in PIN_PROMPT..VERIFY_CARD -> {
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
        if (checkoutUiState.hasError && currentCheckoutStep == CHECKOUT) {
            recordCheckoutEvent(CheckoutEvent.CheckoutPayViewDialogOrderFailed)
        }

        // analytic event for order success dialog displayed
        if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG) {
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
        MOBILE_MONEY -> if (hasFees) CHECKOUT else null
        BANK_CARD -> if (hasFees) CARD_SETUP else null
        HUBTEL_BALANCE -> PIN_PROMPT
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
            HUBTEL_BALANCE -> PAYMENT_STATUS
        }
    } else CHECKOUT
}

private const val TAG = "PayOrderScreen"