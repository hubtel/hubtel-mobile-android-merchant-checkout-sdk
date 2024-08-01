package com.hubtel.merchant.checkout.sdk.ux.pay.order

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.BeginPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordBeginPurchaseEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.BankCardStatus
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.BusinessInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CardStatus
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.VerificationType
import com.hubtel.merchant.checkout.sdk.platform.model.WalletProvider
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBProgressDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBModalBottomSheetLayout
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.UiState2
import com.hubtel.merchant.checkout.sdk.ux.pay.add_mandate.AddMandateScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.add_mandate.UiStates
import com.hubtel.merchant.checkout.sdk.ux.pay.add_wallet.AddWalletScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.bankpay.BankPayReceipt
import com.hubtel.merchant.checkout.sdk.ux.pay.gh_card.GhCardConfirmationScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.gh_card.GhCardVerificationScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CARD_SETUP
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CHECKOUT
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.CHECKOUT_SUCCESS_DIALOG
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.COLLECT_DEVICE_INFO
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.GET_FEES
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.GHANA_CARD_VERIFICATION
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.PAYMENT_COMPLETED
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.PAY_ORDER
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.SELECT_PAYMENT_METHOD
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep.VERIFY_CARD
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.BANK_CARD
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.BANK_PAY
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.MOBILE_MONEY
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType.OTHER_PAYMENT
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.BankPayOption
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ExpandableBankCardOption
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ExpandableMomoOption
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ExpandableOtherPayments
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.ReviewInstallmentsBottomSheet
import com.hubtel.merchant.checkout.sdk.ux.pay.order.components.repaymentScheduleEntries
import com.hubtel.merchant.checkout.sdk.ux.pay.otp.OtpVerifyScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.status.order_placed.OrderPlacedScreen
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity
import com.hubtel.merchant.checkout.sdk.ux.validate_3ds.CardCheckoutWebview
import com.hubtel.merchant.checkout.sdk.ux.validate_3ds.DeviceCollectionWebView
import kotlinx.coroutines.launch
import timber.log.Timber

internal data class PayOrderScreen(
    private val config: CheckoutConfig, private val attempt: VerificationAttempt? = null
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel = viewModel<PayOrderViewModel>(
            factory = PayOrderViewModel.getViewModelFactory(config.apiKey),
        )

        PayOrderScreenContent(viewModel)
    }

    @OptIn(ExperimentalMaterialApi::class)
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
        val enrollUiState by viewModel.enrollUiState

        val ghanaCardUiState by viewModel.ghanaCardUiState

        val paymentChannelsUiState by viewModel.paymentChannelsUiState
        val bankChannels = viewModel.bankChannels
        val momoChannels = viewModel.momoChannels
        val otherChannels = viewModel.otherChannels

        val businessInfoUiState by viewModel.businessInfoUiState

        var showCancelDialog by remember { mutableStateOf(false) }

        val walletUiState = remember { PaymentWalletUiState(null) }

        val bankCardUiState = remember(bankWallets) {
            BankCardUiState(
                wallet = bankWallets.firstOrNull(), useSavedBankCard = bankWallets.isNotEmpty(),
            )
        }

        val momoWalletUiState = remember { MomoWalletUiState() }

        val otherPaymentUiState = remember { OtherPaymentUiState() }

        val bankPayUiState = remember { BankPayUiState() }

        val payIn4UiState = remember { PayIn4UiState() }

        var currentCheckoutStep: CheckoutStep by rememberSaveable {
            if (attempt == null) mutableStateOf(GET_FEES) else {
                momoWalletUiState.mobileNumber = attempt.number
                mutableStateOf(attempt.step ?: CHECKOUT)
            }
//            mutableStateOf(GET_FEES)
        }

        val customerWalletsUiState by viewModel.customerWalletsUiState

        val feeItem = remember(checkoutFeesUiState) {
            checkoutFeesUiState.data ?: CheckoutFee(
                0.0, 0.0, CheckoutType.RECEIVE_MONEY_PROMPT.rawValue, 0.0
            )
        }


        var shouldShowWebView by remember { mutableStateOf(false) }

        var isPayButtonEnabled by remember { mutableStateOf(false) }

        val isLoading by remember { // TODO
            derivedStateOf {
                (cardSetupUiState.isLoading || checkoutUiState.isLoading || currentCheckoutStep == COLLECT_DEVICE_INFO) && currentCheckoutStep in CARD_SETUP..CHECKOUT
            }
        }

        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        val coroutineScope = rememberCoroutineScope()

        HBScaffold(
            topBar = {
                HBTopAppBar(title = {
                    Text(
                        text = stringResource(id = R.string.checkout_heading),
                    )
                }, onNavigateUp = {
                    showCancelDialog = true
                    recordCheckoutEvent(CheckoutEvent.CheckoutPayTapClose)
                })
            },
            bottomBar = {
                Column(Modifier.animateContentSize()) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        thickness = 2.dp,
                    )

                    LoadingTextButton(
//                        text = "${stringResource(R.string.checkout_pay)} ${orderTotal.formatMoney()}",
                        text = when {
                            walletUiState.isBankPay -> stringResource(R.string.checkout_generate_invoice)
                            payIn4UiState.repaymentEntries?.isNotEmpty() == true -> {
                                stringResource(R.string.checkout_pay)
                            }

                            else -> stringResource(R.string.checkout_pay)
                        },
                        onClick = {
                            Timber.d("Wallet Type: ${walletUiState.payOrderWalletType}")
                            Timber.d("Wallet Type2: ${walletUiState.isOtherPaymentWallet}")
                            when {
                                walletUiState.isOtherPaymentWallet -> {
                                    if (otherPaymentUiState.newMandate || (viewModel.getMandateId()
                                            ?.isEmpty() == true && otherPaymentUiState.walletProvider == WalletProvider.GMoney)
                                    ) {
                                        navigator?.push(
                                            AddMandateScreen(
                                                config = config,
                                                walletUiState.payOrderWalletType,
                                                UiStates(
                                                    momoWalletUiState,
                                                    otherPaymentUiState,
                                                    bankCardUiState
                                                )
                                            )
                                        )
                                        return@LoadingTextButton
                                    }
                                }

                                walletUiState.isBankPay -> {
                                    currentCheckoutStep = CHECKOUT
                                }

                            }

                            // If checkout requiresKyc(requireNationalId) && wallet type is momo then check verification details

                            Timber.d("TAP: PAY tapped!")
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
                    fees = listOf(feeItem),
                    amount = config.amount,
                    total = if (orderTotal != 0.0) orderTotal else config.amount,
                    businessInfo = BusinessInfo(
                        businessInfoUiState.data?.businessName,
                        businessInfoUiState.data?.businessLogoURL
                    ),
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

                if (paymentChannelsUiState.isLoading || customerWalletsUiState.isLoading || businessInfoUiState.isLoading) {
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


                AnimatedVisibility(
                    momoChannels.isNotEmpty() && (customerWalletsUiState.data?.isNotEmpty() == true || customerWalletsUiState.hasError)
                ) {
                    // Mobile Money
                    ExpandableMomoOption(
                        state = momoWalletUiState,
                        channels = momoChannels,
                        expanded = walletUiState.isMomoWallet,
                        onExpand = {
                            momoWalletUiState.isWalletSelected = true
                            walletUiState.setWalletType(MOBILE_MONEY)
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
                        },

                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                        wallets = if (businessInfoUiState.data?.isHubtelInternalMerchant == true) customerWalletsUiState.data
                            ?: /*cachedMomoWalletsUiState.data ?:*/ emptyList() else emptyList(),
                        onAddNewTapped = { navigator?.push(AddWalletScreen(config)) }
                    )
                }


                if (bankChannels.isNotEmpty()) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault * 2),
                    )
                }

                AnimatedVisibility(bankChannels.isNotEmpty() /*&& customerWalletsUiState.data?.isNotEmpty() == true*/) {
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
                        isInternalMerchant = businessInfoUiState.data?.isHubtelInternalMerchant == true
                    )
                }

                Divider(
                    color = HubtelTheme.colors.outline,
                    modifier = Modifier.padding(horizontal = Dimens.paddingDefault * 2),
                )

                if (otherChannels.isNotEmpty()) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault * 2),
                    )
                }

                AnimatedVisibility(otherChannels.isNotEmpty() && (customerWalletsUiState.data?.isNotEmpty() == true || customerWalletsUiState.hasError)) {
                    val filteredChannels =
                        if (businessInfoUiState.data?.isHubtelInternalMerchant == true) otherChannels else otherChannels.filter { it != PaymentChannel.HUBTEL }
                    otherPaymentUiState.isHubtelInternalMerchant = true
                    ExpandableOtherPayments(state = otherPaymentUiState,
                        channels = otherChannels,
                        expanded = walletUiState.isOtherPaymentWallet,
                        onExpand = {
                            otherPaymentUiState.isWalletSelected = true
                            walletUiState.setWalletType(OTHER_PAYMENT)
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
                        },
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                        isInternalMerchant = businessInfoUiState.data?.isHubtelInternalMerchant == true,
                        wallets = customerWalletsUiState.data ?: emptyList(),
                        onAddNewTapped = {
                            navigator?.push(AddWalletScreen(config))
                        })
                }

                Divider(
                    color = HubtelTheme.colors.outline,
                    modifier = Modifier.padding(horizontal = Dimens.paddingDefault * 2),
                )

                // BankPay
                AnimatedVisibility(otherChannels.isNotEmpty() && (customerWalletsUiState.data?.isNotEmpty() == true || customerWalletsUiState.hasError)) {
                    BankPayOption(
                        state = bankPayUiState,
                        channels = bankChannels,
                        expanded = walletUiState.isBankPay,
                        onExpand = {
                            bankPayUiState.isWalletSelected = true
                            bankPayUiState.mobileNumber = config.msisdn
                            walletUiState.setWalletType(BANK_PAY)
                            recordCheckoutEvent(CheckoutEvent.CheckoutPayTapMobileMoney)
                        },
                        modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
                        wallets = if (businessInfoUiState.data?.isHubtelInternalMerchant == true) customerWalletsUiState.data
                            ?: emptyList() else emptyList(),
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

        if (checkoutUiState.hasError) {
            CheckoutMessageDialog(
                onDismissRequest = { viewModel.resetCheckoutState() },
                positiveText = stringResource(id = R.string.checkout_okay),
                titleText = stringResource(R.string.checkout_error),
                painter = painterResource(id = R.drawable.checkout_ic_alert_red),
                onPositiveClick = { viewModel.resetCheckoutState() },
                message = checkoutUiState.error?.asString()
            )
        }

        LaunchedEffect(checkoutUiState) {
            if (checkoutUiState.data?.getBankCardStatus == BankCardStatus.AUTHENTICATION_SUCCESSFUL) {
                currentCheckoutStep = PAYMENT_COMPLETED
                shouldShowWebView = false
            }
        }

        if (currentCheckoutStep == COLLECT_DEVICE_INFO) {
            DeviceCollectionWebView(html = cardSetupUiState.data?.html ?: "") {

                coroutineScope.launch {
                    //enroll 3ds here
                    viewModel.enroll3DS(config)
                    if (enrollUiState.success) {
                        currentCheckoutStep = CHECKOUT
                        navigator?.push(
                            CardCheckoutWebview(
                                config = config,
                                html = enrollUiState.data?.html ?: "",
                                onFinish = {
                                    currentCheckoutStep = PAYMENT_COMPLETED
                                    navigator.push(
                                        PaymentStatusScreen(
                                            providerName = paymentInfo?.providerName,
                                            config = config,
                                            checkoutType = checkoutFeesUiState.data?.getCheckoutType
                                        )
                                    )
                                }
                            )
                        )
                    }

                }
                //start enrollment here.
            }

            Timber.d("Custom Data: ${checkoutUiState.data?.customData}")
            Timber.d("Bank Card Status: ${checkoutUiState.data?.getBankCardStatus}")
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

        if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG && checkoutFeesUiState.data?.getCheckoutType == CheckoutType.DIRECT_DEBIT) {
            currentCheckoutStep = PAYMENT_COMPLETED
            navigator?.push(
                PaymentStatusScreen(
                    providerName = paymentInfo?.providerName,
                    config = config,
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType
                )
            )
        }

        if (attempt?.checkoutType == CheckoutType.DIRECT_DEBIT) {
            currentCheckoutStep = PAYMENT_COMPLETED
            navigator?.push(
                PaymentStatusScreen(
                    providerName = paymentInfo?.providerName,
                    config = config,
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType
                )
            )
        }

        if (attempt?.checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT && attempt.attempt) {

            CheckoutMessageDialog(
                onDismissRequest = {},
                titleText = stringResource(R.string.checkout_success),
                message = stringResource(
                    R.string.checkout_momo_bill_prompt_msg,
                    paymentInfo?.accountNumber ?: attempt.number ?: "",
                ),
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = {
                    currentCheckoutStep = PAYMENT_COMPLETED
//                    walletUiState.payOrderWalletType?.let { walletType ->
//                        viewModel.payOrder(config, walletType)
//                    }
                    navigator?.push(
                        PaymentStatusScreen(
                            providerName = paymentInfo?.providerName,
                            config = config,
                            checkoutType = checkoutFeesUiState.data?.getCheckoutType
                        )
                    )
                },
            )
        }
        if (attempt?.checkoutType == CheckoutType.RECEIVE_MONEY_PROMPT && !attempt.attempt) {
            currentCheckoutStep = PAYMENT_COMPLETED
            navigator?.push(
                PaymentStatusScreen(
                    providerName = paymentInfo?.providerName,
                    config = config,
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType
                )
            )
        }

        if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG && checkoutFeesUiState.data?.getCheckoutType == CheckoutType.RECEIVE_MONEY_PROMPT && !walletUiState.isOtherPaymentWallet) {
            CheckoutMessageDialog(
                onDismissRequest = {},
                titleText = stringResource(R.string.checkout_success),
                message = stringResource(
                    R.string.checkout_momo_bill_prompt_msg,
                    paymentInfo?.accountNumber ?: "",
                ),
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = {
                    currentCheckoutStep = PAYMENT_COMPLETED
                    navigator?.push(
                        PaymentStatusScreen(
                            providerName = paymentInfo?.providerName,
                            config = config,
                            checkoutType = checkoutFeesUiState.data?.getCheckoutType
                        )
                    )
                },
            )
        }

        if (currentCheckoutStep == CHECKOUT_SUCCESS_DIALOG && checkoutFeesUiState.data?.getCheckoutType == CheckoutType.RECEIVE_MONEY_PROMPT && walletUiState.isOtherPaymentWallet) {
            currentCheckoutStep = PAYMENT_COMPLETED
            navigator?.push(
                PaymentStatusScreen(
                    providerName = paymentInfo?.providerName,
                    config = config,
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType
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


        if (businessInfoUiState.data?.requireNationalID == true && currentCheckoutStep == PAY_ORDER && attempt == null) {
            currentCheckoutStep =
                if (walletUiState.isBankCard || walletUiState.isOtherPaymentWallet || walletUiState.isBankPay) {
                    PAY_ORDER
                } else {
                    GHANA_CARD_VERIFICATION
                }
        }

        if (currentCheckoutStep == GHANA_CARD_VERIFICATION && ghanaCardUiState.data?.getCardStatus == CardStatus.VERIFIED) {
            viewModel.resetGhanaCardState()
            currentCheckoutStep = CHECKOUT
        }

        if (currentCheckoutStep == GHANA_CARD_VERIFICATION && ghanaCardUiState.data?.getCardStatus == CardStatus.UN_VERIFIED) {
            viewModel.resetGhanaCardState()
            currentCheckoutStep = CHECKOUT
            navigator?.push(
                GhCardConfirmationScreen(
                    config,
                    momoWalletUiState.mobileNumber ?: "",
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType
                )
            )
        }

        if (currentCheckoutStep == GHANA_CARD_VERIFICATION /*&& ghanaCardUiState.error == UiText.DynamicString(
                "Not Found"
            )*/ && ghanaCardUiState.hasError
        ) {
            viewModel.resetGhanaCardState()
            currentCheckoutStep = CHECKOUT
            navigator?.push(
                GhCardVerificationScreen(
                    config,
                    momoWalletUiState.mobileNumber ?: "",
                    checkoutType = checkoutFeesUiState.data?.getCheckoutType,
                )
            )
            return
        }

        LaunchedEffect(isLoading) {
            if (!isLoading && currentCheckoutStep == CHECKOUT) {
                if (checkoutFeesUiState.data?.getCheckoutType == CheckoutType.PRE_APPROVAL_CONFIRM && walletUiState.isMomoWallet) {
                    navigator?.push(
                        OrderPlacedScreen(
                            walletName = "Mobile Money Wallet",
                            amount = checkoutFeesUiState.data?.amountPayable
                        )
                    )
                }
            }
        }

        LaunchedEffect(Unit) {
            viewModel.getCustomerWalletsAndPaymentChannels(config)
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
            bankCardUiState.isValidYear,
            momoWalletUiState.walletProvider,
            momoWalletUiState.mobileNumber,
            momoWalletUiState.isWalletSelected,
            otherPaymentUiState.walletProvider,
            otherPaymentUiState.mobileNumber,
            otherPaymentUiState.isWalletSelected,
            otherPaymentUiState.isHubtelInternalMerchant,
        ) {
            // update payment info object when user checkout input
            // changes
            if (attempt != null) {
                walletUiState.setWalletType(attempt.walletType)
            }
            val walletType: PayOrderWalletType =
                walletUiState.payOrderWalletType ?: return@LaunchedEffect

            isPayButtonEnabled = when (walletType) {
                MOBILE_MONEY -> momoWalletUiState.isValid || (businessInfoUiState.data?.isHubtelInternalMerchant == true && momoWalletUiState.isWalletSelected) // modified
                BANK_CARD -> bankCardUiState.isValid && bankCardUiState.isValidYear
                OTHER_PAYMENT -> otherPaymentUiState.isValid || (businessInfoUiState.data?.isHubtelInternalMerchant == true && otherPaymentUiState.isWalletSelected) // modified
                BANK_PAY -> bankPayUiState.isValid
            }

            viewModel.getGhanaCardDetails(config, momoWalletUiState.mobileNumber ?: "")

            viewModel.updatePaymentInfo(
                walletType,
                momoWalletUiState,
                otherPaymentUiState,
                bankCardUiState,
                bankPayUiState
            )

            // ignore state change if we're not in the get
            // fees state or user wallet is hubtel balance
//            if (currentCheckoutStep != GET_FEES) return@LaunchedEffect
            if (currentCheckoutStep != GET_FEES) return@LaunchedEffect

//            if ((walletUiState.isBankCard && !bankCardUiState.isValid) || (walletUiState.isMomoWallet && !momoWalletUiState.isValid)) return@LaunchedEffect
            if ((walletUiState.isBankCard && !bankCardUiState.isValid)
                || (walletUiState.isMomoWallet && !momoWalletUiState.isValid)
                || (walletUiState.isOtherPaymentWallet && !otherPaymentUiState.isValid)
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

        Timber.d("TAP [CASES]: $currentCheckoutStep")

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
                    if (attempt == null) {
                        walletUiState.payOrderWalletType?.let { walletType ->
                            viewModel.payOrder(config, walletType)
                        }

                        if (checkoutUiState.data?.getBankCardStatus == BankCardStatus.AUTHENTICATION_SUCCESSFUL) {
                            currentCheckoutStep = PAYMENT_COMPLETED
                        }
                    } else {
                        walletUiState.setWalletType(attempt.walletType)
                        walletUiState.payOrderWalletType?.let { walletType ->
                            viewModel.payOrder(config, walletType)
                        }
                        currentCheckoutStep = PAYMENT_COMPLETED
                    }

                }

                PAYMENT_COMPLETED -> {
                    if (checkoutFeesUiState.data?.getCheckoutType == CheckoutType.PRE_APPROVAL_CONFIRM && walletUiState.isMomoWallet) {
                        navigator?.push(
                            OrderPlacedScreen(
                                walletName = "Mobile Money Wallet",
                                amount = checkoutFeesUiState.data?.amountPayable
                            )
                        )
                    }

                    if (checkoutFeesUiState.data?.getCheckoutType == CheckoutType.DIRECT_DEBIT && checkoutUiState.data?.skipOtp == false && checkoutUiState.data?.getVerificationType == VerificationType.OTP /* && walletUiState.isMomoWallet*/) {
                        navigator?.push(OtpVerifyScreen(config, checkoutUiState.data!!))
                    }

                    if (checkoutFeesUiState.data?.getCheckoutType == CheckoutType.DIRECT_DEBIT) {
                        Timber.d("TAP [INSIDE]: $currentCheckoutStep - DIRECT_DEBIT")
                    }

                    if (walletUiState.isBankCard) {
                        navigator?.push(
                            PaymentStatusScreen(
                                providerName = paymentInfo?.providerName,
                                config = config,
                                checkoutType = null
                            )
                        )
                    }

                    if (walletUiState.isBankPay) {
                        navigator?.push(
                            BankPayReceipt(
                                config,
                                checkoutUiState.data,
                                businessInfoUiState.data
                            )
                        )
                    }

                    return@LaunchedEffect
                }


                else -> {}
            }
        }

        LaunchedEffect(Unit) {
            val orderItems = listOf(config.toPurchaseOrderItem())

            recordBeginPurchaseEvent(
                BeginPurchaseEvent(amount = config.amount,
                    purchaseOrderItems = orderItems,
                    purchaseOrderItemNames = orderItems.map { it.name ?: "" },
                    purchaseOrderProviders = orderItems.map { it.provider ?: "" })
            )
        }

        BackHandler {
            when (currentCheckoutStep) {
                CHECKOUT_SUCCESS_DIALOG, in CARD_SETUP..VERIFY_CARD -> {
                }

                else -> {
                    showCancelDialog = true
                }
            }
        }
    }


    private fun getNextStepAfterPayOrder(
        walletType: PayOrderWalletType, checkoutFeeUiState: UiState2<CheckoutFee>
    ): CheckoutStep {
        val hasFees = checkoutFeeUiState.success && !checkoutFeeUiState.isLoading

        val nextStep = when (walletType) {
            MOBILE_MONEY -> if (hasFees) CHECKOUT else null
            BANK_CARD -> if (hasFees) CARD_SETUP else null
            OTHER_PAYMENT -> if (hasFees) CHECKOUT else null
            BANK_PAY -> if (hasFees) CHECKOUT else null
        }

        // if next is null go back down to get fees
        return nextStep ?: GET_FEES
    }

    private fun getNextStepAfterCardSetup(
        cardSetupUiState2: UiState2<ThreeDSSetupInfo>
    ): CheckoutStep {
        return if (cardSetupUiState2.success && cardSetupUiState2.hasData && !cardSetupUiState2.isLoading) {
            COLLECT_DEVICE_INFO
        } else CARD_SETUP
    }

    private fun getNextStepAfterCheckout(
        walletType: PayOrderWalletType, checkoutUiState: UiState2<CheckoutInfo>
    ): CheckoutStep {
        return if (checkoutUiState.success && checkoutUiState.hasData && !checkoutUiState.isLoading) {
            when (walletType) {
                BANK_CARD -> VERIFY_CARD
                MOBILE_MONEY -> CHECKOUT_SUCCESS_DIALOG
                OTHER_PAYMENT -> CHECKOUT_SUCCESS_DIALOG // TODO: might need further looking into
//                BANK_PAY -> CHECKOUT_SUCCESS_DIALOG
                BANK_PAY -> PAYMENT_COMPLETED
            }
        } else CHECKOUT
    }
}

internal data class VerificationAttempt(
    val attempt: Boolean = false,
    val number: String = "",
    val step: CheckoutStep? = null,
    val checkoutType: CheckoutType? = null,
    val walletType: PayOrderWalletType? = null
)
