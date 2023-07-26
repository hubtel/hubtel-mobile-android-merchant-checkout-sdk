package com.hubtel.sdk.checkout.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
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
import com.hubtel.core_network.model.response.UserCardDetailsInfo
import com.hubtel.core_ui.components.custom.HBMessageDialog
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.sdk.checkout.R
import com.hubtel.sdk.checkout.ui.components.CheckoutReceiptCard
import com.hubtel.sdk.checkout.ui.components.ExpandableBankCardOption
import com.hubtel.sdk.checkout.ui.components.LoadingTealTextButton
import com.hubtel.sdk.checkout.ui.components.VerificationDialog3ds

//@Composable
//fun CheckoutScreen(viewModel: CheckoutScreenViewModel) {
//
//
//    val userCardDetailsUiState by viewModel.userCardDetailsState
//    val cardEnrollmentUiState by viewModel.cardEnrollmentState
//
//
//    var isPayButtonEnabled by remember { mutableStateOf(false) }
//
//    val bankWallets = viewModel.bankWallets
//
//    var currentCheckoutStep: CheckoutStep by remember { mutableStateOf(CheckoutStep.CARD_SETUP) }
//
//    val bankCardUiState = remember(bankWallets) {
//        BankCardUiStates(
//            wallet = bankWallets.firstOrNull(),
//            useSavedBankCard = bankWallets.isNotEmpty()
//        )
//    }
//    val shouldShowWebView = remember(
//        currentCheckoutStep,
//    ) {
//        currentCheckoutStep == CheckoutStep.COLLECT_DEVICE_INFO
//    }
//
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//    var bankExpanded by remember {
//        mutableStateOf(false)
//    }
//
//
//    val isLoading by remember {
//        derivedStateOf {
//            (userCardDetailsUiState.isLoading
//                    || cardEnrollmentUiState.isLoading
//                    || currentCheckoutStep == CheckoutStep.COLLECT_DEVICE_INFO)
//                    && currentCheckoutStep == CheckoutStep.CARD_SETUP
//        }
//    }
//
//
//    var showCancelDialog by remember { mutableStateOf(false) }
//    HBScaffold(
//        modifier = Modifier.safeDrawingPadding(),
//        topBar = {
//            Box(Modifier.fillMaxWidth()) {
//                IconButton(
//                    onClick = {
//                        showCancelDialog = true
//                    },
//                    modifier = Modifier.align(Alignment.CenterEnd)
//                ) {
//                    Icon(
//                        painter = painterResource(R.drawable.core_ic_close_circle),
//                        contentDescription = stringResource(R.string.close),
//                    )
//                }
//            }
//        },
//        bottomBar = {
//            Column(Modifier.animateContentSize()) {
//                Divider(
//                    color = HubtelTheme.colors.outline,
//                    thickness = 2.dp,
//                )
//
//                LoadingTealTextButton(
//                    text = "PAY NOW",
//                    onClick = {
//                        viewModel.setup3DS(
//                            amount = 10,
//                            expiryMonth = bankCardUiState.month,
//                            cardHolderName = bankCardUiState.cardName,
//                            expiryYear = bankCardUiState.year,
//                            cvv = bankCardUiState.cvv,
//                            cardNumber = bankCardUiState.cardNumber
//                        )
////                        currentCheckoutStep = CheckoutStep.PAY_ORDER
//                    },
//                    enabled = isPayButtonEnabled,
//                    loading = false,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .animateContentSize()
//                        .padding(16.dp),
//                )
//            }
//        },
//        backgroundColor = HubtelTheme.colors.uiBackground,
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .verticalScroll(rememberScrollState())
//        ) {
//            CheckoutReceiptCard(
//                logoUrl = "https://i0.wp.com/thebftonline.com/wp-content/uploads/2021/08/vodafone-cash-ghana-min.jpg?fit=1200%2C1200&ssl=1",
//                accountName = "David Glover",
//                accountNumber = "Vodafone Cash",
//                serviceName = "Vodafone Cash",
//                amount = 20.00,
//                total = 40.00,
//                modifier = Modifier
//                    .padding(12.dp)
//                    .animateContentSize()
//            )
//
//            Text(
//                text = "Pay with",
//                style = HubtelTheme.typography.h2,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = Dimens.paddingDefault)
//                    .padding(horizontal = Dimens.paddingDefault)
//                    .background(
//                        color = HubtelTheme.colors.cardBackground,
//                        shape = HubtelTheme.shapes.medium.copy(
//                            bottomStart = CornerSize(0.dp),
//                            bottomEnd = CornerSize(0.dp),
//                        ),
//                    )
//                    .padding(Dimens.paddingDefault),
//            )
//
//            // Bank Card
//            ExpandableBankCardOption(
//                state = bankCardUiState,
//                wallets = bankWallets,
//                expanded = bankExpanded,
//                onExpand = {
//                    bankExpanded = !bankExpanded
//                },
//                modifier = Modifier.padding(horizontal = Dimens.paddingDefault),
//            )
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = Dimens.paddingDefault)
//                    .background(
//                        color = HubtelTheme.colors.cardBackground,
//                        shape = HubtelTheme.shapes.medium.copy(
//                            topStart = CornerSize(0.dp),
//                            topEnd = CornerSize(0.dp),
//                        )
//                    )
//                    .padding(top = Dimens.paddingDefault),
//            )
//
//        }
//    }
//
//    if (isLoading) {
//        HBProgressDialog(
//            message = "${stringResource(R.string.fc_please_wait)}..."
//        )
//    }
//
//    if (showCancelDialog) {
//        HBMessageDialog(
//            onDismissRequest = { showCancelDialog = false },
//            titleText = stringResource(R.string.cancel_transaction),
//            message = stringResource(R.string.do_you_want_to_cancel_this_transaction),
//            positiveText = stringResource(R.string.yes),
//            negativeText = stringResource(R.string.no),
//            onPositiveClick = { showCancelDialog = false },
//            onNegativeClick = { },
//        )
//    }
//
//    if (shouldShowWebView) {
//        VerificationDialog3ds(
//            step = currentCheckoutStep,
//            setupState = remember(userCardDetailsUiState) {
//                val threeDSSetupInfo = userCardDetailsUiState.data
//
//                ThreeDSSetupState(
//                    accessToken = threeDSSetupInfo?.accessToken,
//                    transactionId = threeDSSetupInfo?.transactionId
//                )
//            },
//            verificationState = remember(cardEnrollmentUiState) {
//                val checkoutInfo = cardEnrollmentUiState.data
//
//                Verfication3dsState(
//                    jwt = checkoutInfo?.jwt,
//                    customData = checkoutInfo?.customData
//                )
//            },
////            onCollectionComplete = { currentCheckoutStep = CheckoutStep.CHECKOUT },
//            onCollectionComplete = {},
////            onCardVerified = { currentCheckoutStep = CheckoutStep.PAYMENT_STATUS },
//            onCardVerified = {},
////            onBackClick = {
////                if (currentCheckoutStep == CheckoutStep.VERIFY_CARD) {
////                    showCancelDialog = true
////                }
////            },
//            onBackClick = {
////                if (currentCheckoutStep == CheckoutStep.VERIFY_CARD) {
////                    showCancelDialog = true
////                }
//            },
//        )
//    }
//
//    LaunchedEffect(userCardDetailsUiState,cardEnrollmentUiState){
//        val nextStep = when (currentCheckoutStep) {
//            CheckoutStep.CARD_SETUP -> getNextStepAfterCardSetup(userCardDetailsUiState)
//            else -> null
//        }
//
//        if (nextStep != null) {
//            currentCheckoutStep = nextStep
//        }
//    }
//
//    LaunchedEffect(
//        bankCardUiState.useSavedBankCard,
//        bankCardUiState.saveForLater,
//        bankCardUiState.selectedWallet,
//        bankCardUiState.cardNumber,
//        bankCardUiState.cardName,
//        bankCardUiState.monthYear,
//        bankCardUiState.cvv
//    ) {
//        isPayButtonEnabled = bankCardUiState.isValid
//    }
//
//    LaunchedEffect(userCardDetailsUiState){
//        if(userCardDetailsUiState.hasData){
//            viewModel.enroll3DS()
//        }
//    }
//}
//
//private fun getNextStepAfterCardSetup(
//    cardSetupUiState2: UiState2<UserCardDetailsInfo>
//): CheckoutStep {
//    return if (cardSetupUiState2.success
//        && cardSetupUiState2.hasData &&
//        !cardSetupUiState2.isLoading
//    ) {
//        CheckoutStep.COLLECT_DEVICE_INFO
//    } else CheckoutStep.CARD_SETUP
//}