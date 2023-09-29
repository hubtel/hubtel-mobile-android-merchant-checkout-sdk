package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderWalletType
import com.hubtel.merchant.checkout.sdk.ux.pay.order.VerificationAttempt
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme

internal data class GhCardConfirmationScreen(
    val config: CheckoutConfig,
    val phoneNumber: String,
    val cardNumber: String? = null,
    val unverified: Boolean,
    val checkoutType: CheckoutType? = null
) :
    Screen {
    @Composable
    override fun Content() {

        val viewModel = viewModel<GhCardConfirmationViewModel>(
            factory = GhCardConfirmationViewModel.getViewModelFactory(config.apiKey),
        )

        ScreenContent(viewModel = viewModel)
    }

    @Composable
    private fun ScreenContent(viewModel: GhCardConfirmationViewModel) {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        var isButtonEnabled by remember {
            mutableStateOf(false)
        }

        val ghanaCardUiState by viewModel.ghanaCardUiState

        val isLoading by remember {
            derivedStateOf { ghanaCardUiState.isLoading }
        }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(
                title = {
                    Text(text = "Verification")
                },
                /*onNavigateUp = {
                    navigator?.pop()
                }*/
            )
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {

                LoadingTextButton(
                    text = "CONFIRM", onClick = {
                        // TODO: uncomment below
//                        viewModel.confirmGhanaCard(
//                            config,
//                            phoneNumber
//                        )

                        navigator?.push(
                            PayOrderScreen(
                                config = config,
                                attempt = VerificationAttempt(
                                    true,
                                    number = phoneNumber,
                                    step = CheckoutStep.CHECKOUT,
                                    checkoutType = checkoutType,
                                    walletType = PayOrderWalletType.MOBILE_MONEY
                                )
                            )
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall),
                    enabled = isButtonEnabled
                )
            }
        }) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(Dimens.paddingDefault)
            ) {
                if (ghanaCardUiState.hasData) {
                    Box(modifier = Modifier.padding(Dimens.paddingDefault))
                    Image(
                        painter = painterResource(R.drawable.checkout_verification_success),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(bottom = Dimens.paddingSmall)
                            .size(75.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "Your account has been verified successfully",
                        style = HubtelTheme.typography.h3.copy(textAlign = TextAlign.Center),
                        modifier = Modifier
                            .padding(top = Dimens.paddingSmall, bottom = Dimens.paddingLarge)
                            .align(Alignment.CenterHorizontally)
                    )
                    GhanaCard(cardDetails = ghanaCardUiState.data)
                }
            }

        } // HBScaffold

        if (isLoading) {
            HBProgressDialog(
                message = "${stringResource(id = R.string.checkout_please_wait)}...",
                progressColor = CheckoutTheme.colors.colorPrimary,
            )
        }

        LaunchedEffect(isLoading) {
            if (!isLoading) {
                isButtonEnabled = true
            }
        }

        LaunchedEffect(Unit) {
            if (unverified) {
                viewModel.getGhanaCardDetails(config, phoneNumber)
            } else {
                viewModel.addGhanaCard(config, phoneNumber, cardNumber ?: "")
            }
        }

    }

    @Composable
    private fun GhanaCard(cardDetails: GhanaCardResponse?) {
        val constraints = ConstraintSet {
            val imageConstraints = createRefFor("armsCoat")

            constrain(imageConstraints) {
                top.linkTo(parent.top)
                end.linkTo(parent.end, -(Dimens.paddingDefault))
                width = Dimension.value(150.dp)
                height = Dimension.value(150.dp)
            }
        }

        ConstraintLayout(
            constraints,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFEDFAF7), shape = RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(R.drawable.checkout_coat_of_arms),
                contentDescription = null,
                modifier = Modifier.layoutId("armsCoat"),
            )

            CardContent(cardDetails = cardDetails)

        }
    }

    @Composable
    private fun CardContent(cardDetails: GhanaCardResponse?, modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(Dimens.paddingDefault)) {
                Text(text = "Ghana Card Details", style = HubtelTheme.typography.h3)
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                CardLabel(label = "Full Name", info = cardDetails?.fullName ?: "")
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                CardLabel(label = "Personal ID Number", info = cardDetails?.nationalID ?: "")
                Box(modifier = Modifier.padding(Dimens.paddingSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CardLabel(label = "DOB", info = cardDetails?.dateOfBirth ?: "")
                    CardLabel(
                        label = "Gender",
                        info = cardDetails?.gender ?: "",
                        modifier = Modifier.padding(end = Dimens.paddingLarge)
                    )
                }
            }
        }
    }

    @Composable
    private fun CardLabel(label: String, info: String, modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            Text(text = label, style = HubtelTheme.typography.body2)
            Box(modifier = Modifier.padding(Dimens.paddingNano))
            Text(
                text = info, style = HubtelTheme.typography.h3
            )
        }
    }
}