package com.hubtel.merchant.checkout.sdk.ux.pay.otp

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.model.UiText
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.PayOrderScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.status.PaymentStatusScreen
import com.hubtel.merchant.checkout.sdk.ux.pay.status.finishWithResult

internal data class OtpVerifyScreen(val config: CheckoutConfig, val checkoutInfo: CheckoutInfo) :
    Screen {
    @Composable
    override fun Content() {
        val viewModel =
            viewModel<OtpVerifyViewModel>(
                factory = OtpVerifyViewModel.getViewModelFactory(config.apiKey),
            )

        ScreenContent(viewModel = viewModel)
    }

    @Composable
    private fun ScreenContent(viewModel: OtpVerifyViewModel) {

        val otpUiState by viewModel.otpUiState

        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        var isButtonEnabled by remember { mutableStateOf(false) }

        var showErrorMessage by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        var otpValue by remember {
            mutableStateOf("")
        }

        HBScaffold(backgroundColor = HubtelTheme.colors.uiBackground2, topBar = {
            HBTopAppBar(title = {
                Text(text = "Verify")
            }, onNavigateUp = {
                checkoutActivity?.finishWithResult()
            })
        }, bottomBar = {
            Column(modifier = Modifier.animateContentSize()) {

                LoadingTextButton(
                    text = "VERIFY", enabled = isButtonEnabled, onClick = {
//                    val req = OtpReq(
//                        config.msisdn ?: "",
//                        checkoutInfo.hubtelPreapprovalId ?: "",
//                        config.clientReference ?: "",
//                        "${checkoutInfo.otpPrefix}-$otpValue"
//                    )
                        viewModel.verify(config, checkoutInfo, otpValue)
                        recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                    }, loading = isLoading, modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .padding(Dimens.paddingSmall)
                )
            }
        }) {
            Column(
                horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(Dimens.paddingDefault)
            ) {
                Box(modifier = Modifier.padding(Dimens.paddingLarge))
                Image(
                    painter = painterResource(R.drawable.checkout_ic_hubtel),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = Dimens.paddingLarge)
                        .size(45.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Text(text = "Enter the verification code sent to ")
                Text(text = "${checkoutInfo.customerMsisdn} starting with ${checkoutInfo.otpPrefix}")

                Box(modifier = Modifier.padding(Dimens.paddingNano))

                OtpTextField(
                    otpText = otpValue, otpCount = 4,
                    onOtpTextChange = { value, _ ->
                        otpValue = value
                    }
                )

                Box(modifier = Modifier.padding(Dimens.paddingNano))

                if (showErrorMessage) {
                    Text(text = "The OTP you have entered is incorrect", color = Color(0xFFFF3344))

                    CheckoutMessageDialog(
                        onDismissRequest = {},
                        painter = painterResource(id = R.drawable.checkout_ic_close_circle_white),
                        titleText = "Error",
                        message = "Kindly try again.",
                        positiveText = stringResource(R.string.checkout_okay),
                        onPositiveClick = {
                            showErrorMessage = false
                            navigator?.push(
                                PayOrderScreen(config = config)
                            ) // TODO: would produce duplicate client reference error
                        },
                        properties = DialogProperties(
                            dismissOnBackPress = false, dismissOnClickOutside = false
                        )
                    )
                }
            }
        }

        LaunchedEffect(otpValue) {
            if (otpValue.length == 4) {
//                val req = OtpReq(
//                    config.msisdn ?: "",
//                    checkoutInfo.hubtelPreapprovalId ?: "",
//                    config.clientReference ?: "",
//                    "${checkoutInfo.otpPrefix}-$otpValue"
//                )
                isButtonEnabled = true
                viewModel.verify(config, checkoutInfo, otpValue)
//                navigator?.push(PaymentStatusScreen(providerName = "", config = config, checkoutType = CheckoutType.DIRECT_DEBIT))

            }
        }

        LaunchedEffect(otpUiState) {
            if (otpUiState.hasData) {
                navigator?.push(
                    PaymentStatusScreen(
                        providerName = "",
                        config = config,
                        checkoutType = CheckoutType.DIRECT_DEBIT
                    )
                )
            }

            if (otpUiState.error == UiText.DynamicString(
                    "OTP Verification Failed. Try again"
                )
            ) {
                showErrorMessage = true
                isLoading = false
            }
        }
    }

    @Composable
    fun OtpTextField(
        modifier: Modifier = Modifier,
        otpText: String,
        otpCount: Int = 6,
        onOtpTextChange: (String, Boolean) -> Unit
    ) {
        LaunchedEffect(Unit) {
            if (otpText.length > otpCount) {
                throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
            }
        }

        BasicTextField(
            modifier = modifier,
            value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
            onValueChange = {
                if (it.text.length <= otpCount) {
                    onOtpTextChange.invoke(it.text, it.text.length == otpCount)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            decorationBox = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(otpCount) { index ->
                        CharView(
                            index = index,
                            text = otpText,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        )
    }

    @Composable
    private fun CharView(
        modifier: Modifier = Modifier,
        index: Int,
        text: String
    ) {
        val isFocused = text.length == index
        val char = when {
            index == text.length -> "•"
            index > text.length -> "•"
            else -> text[index].toString()
        }
        Text(
            modifier = modifier
                .background(
                    color = Color(0xFFE6EAED),
                    shape = RoundedCornerShape(Dimens.paddingNano)
                )
                .width(64.dp)
                .height(64.dp)
                .border(
                    1.dp, when {
                        isFocused -> Color(0xFF01C7B1)
                        else -> Color(0xFF9CABB8)
                    }, RoundedCornerShape(Dimens.paddingNano)
                )
                .padding(top = Dimens.paddingDefault),
            text = char,
            style = TextStyle(fontSize = 20.sp),
            color = if (isFocused) {
                Color(0xFF9CABB8)
//            Color.Blue
            } else {
                Color.DarkGray
//            Color.Red
            },
            textAlign = TextAlign.Center
        )
    }
}