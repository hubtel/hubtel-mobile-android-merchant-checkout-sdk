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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.components.custom.TealButton
import com.hubtel.core_ui.extensions.LocalActivity
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.status.finishWithResult
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme

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

        val screenHeight = LocalConfiguration.current.screenHeightDp
        val density = LocalDensity.current.density
        val maxHeight = (screenHeight / density).dp

        val context = LocalContext.current
        val activity = LocalActivity.current
    val navigator = LocalNavigator.current
    val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        var isButtonEnabled by remember {
            mutableStateOf(false)
        }

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
                TealButton(
                    onClick = {
                        recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.paddingSmall),
                    enabled = isButtonEnabled
                ) {
                    Text(
                        text = "VERIFY",
                        modifier = Modifier
                            .animateContentSize()
                            .padding(Dimens.paddingSmall)
                    )
                }
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
                    onOtpTextChange = { value, otpInputFilled ->
                        otpValue = value
                        if (otpInputFilled) {
                            isButtonEnabled = otpInputFilled
                        }
                    }
                )

                if (isButtonEnabled) {
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
            }
        }

        LaunchedEffect(isButtonEnabled) {
            val req = OtpReq(
                customerMsisdn = checkoutInfo.customerMsisdn ?: "",
                hubtelPreApprovalID = checkoutInfo.hubtelPreapprovalId ?: "",
                clientReferenceID = checkoutInfo.clientReference ?: "",
                otpCode = otpValue
            )
            viewModel.verify(config, req)
        }

        LaunchedEffect(otpUiState) {
            if (otpUiState.success) {

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
            index == text.length -> ""
            index > text.length -> ""
            else -> text[index].toString()
        }
        Text(
            modifier = modifier
                .background(color = Color(0xFFE6EAED))
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