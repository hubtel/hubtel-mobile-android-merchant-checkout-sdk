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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.sections.CheckoutEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordCheckoutEvent
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.CheckoutMessageDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBProgressDialog
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.components.LoadingTextButton
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.status.finishWithResult
import com.hubtel.merchant.checkout.sdk.ux.theme.CheckoutTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity
import kotlinx.coroutines.launch

internal data class OtpVerifyScreen(
    val config: CheckoutConfig,
    val customerMsisdn: String,
    val otpPrefix: String,
    val otpRequestId: String,
    val clientReference: String,
    val preApprovalId: String,
    val paymentChannel: String,
    val onFinish: () -> Unit
) :
    Screen {
    @Composable
    override fun Content() {
        val viewModel =
            viewModel<OtpVerifyViewModel>(
                factory = OtpVerifyViewModel.getViewModelFactory(config.apiKey),
            )

        ScreenContent(viewModel = viewModel, onFinish)
    }

    @Composable
    private fun ScreenContent(
        viewModel: OtpVerifyViewModel,
        onFinish: () -> Unit
    ) {

        val otpUiState by viewModel.otpUiState

        val activity = LocalActivity.current
        val navigator = LocalNavigator.current
        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }
        val coroutineScope = rememberCoroutineScope()

        var isButtonEnabled by remember { mutableStateOf(false) }

        var showErrorMessage by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        var otpValue by remember {
            mutableStateOf("")
        }

        HBScaffold(
            backgroundColor = HubtelTheme.colors.uiBackground2,
            topBar = {
                HBTopAppBar(
                    title = { Text(text = "Verify") },
                    onNavigateUp = { checkoutActivity?.finishWithResult() })
            },
            bottomBar = {
                Column(modifier = Modifier.animateContentSize()) {

                    LoadingTextButton(
                        text = "VERIFY",
                        enabled = isButtonEnabled,
                        loading = isLoading,
                        onClick = {
                            coroutineScope.launch {
                                verifyOtp(
                                    viewModel,
                                    otpValue,
                                    onVerificationFinish = { isSuccessful ->
                                        if (isSuccessful) {
                                            onFinish.invoke()
                                            navigator?.pop()
                                        } else {
                                            showErrorMessage = true
                                        }
                                    }
                                )
                            }
                            recordCheckoutEvent(CheckoutEvent.CheckoutPaymentSuccessfulTapButtonDone)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(Dimens.paddingSmall)
                    )
                }
            }) {
            Column(
                horizontalAlignment = Alignment.Start,
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

                VerifyMsgText(
                    phoneNumber = customerMsisdn.formatInternational(),
                    requestCode = otpPrefix,
                    modifier = Modifier.padding(bottom = Dimens.paddingSmall)
                )

                Box(modifier = Modifier.padding(Dimens.paddingNano))

                OtpTextField(
                    otpText = otpValue,
                    otpCount = 4,
                    onOtpTextChange = { value, _ -> otpValue = value }
                )

                Box(modifier = Modifier.padding(Dimens.paddingNano))

                if (showErrorMessage) {
                    CheckoutMessageDialog(
                        onDismissRequest = {
                            showErrorMessage = false
                        },
                        painter = painterResource(id = R.drawable.checkout_ic_close_circle_white),
                        titleText = "Error",
                        message = "OTP Verification failed.Kindly try again.",
                        positiveText = stringResource(R.string.checkout_okay),
                        onPositiveClick = {
                            showErrorMessage = false
                        },
                    )
                }

                if (viewModel.paymentOtpUiState.value.isLoading) {
                    HBProgressDialog(
                        message = "${stringResource(R.string.checkout_please_wait)}...",
                        progressColor = CheckoutTheme.colors.colorPrimary,
                    )
                }
            }
        }

        LaunchedEffect(otpValue) {
            if (otpValue.length == 4) {
                isButtonEnabled = true

                verifyOtp(
                    viewModel,
                    otpValue,
                    onVerificationFinish = { isSuccessful ->
                        if (isSuccessful) {
                            onFinish.invoke()
                            navigator?.pop()
                        } else {
                            showErrorMessage = true
                        }
                    }
                )
            }
        }
    }

    private suspend fun verifyOtp(
        viewModel: OtpVerifyViewModel,
        otpValue: String,
        onVerificationFinish: (Boolean) -> Unit,
    ) {
        viewModel.verify(
            salesId = config.posSalesId ?: "",
            customerMsisdn = customerMsisdn.formatInternational(),
            userOtpEntry = otpValue,
            otpPrefix = otpPrefix,
            otpRequestId = "",
            clientReference = clientReference,
            preApprovalId = preApprovalId,
            paymentChannel = paymentChannel
        )

        onVerificationFinish.invoke(viewModel.otpUiState.value.success)
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


@Composable
fun VerifyMsgText(
    phoneNumber: String,
    requestCode: String,
    modifier: Modifier = Modifier
) {
    val regularStyle = SpanStyle(
        color = Color.Black
    )

    val boldStyle = SpanStyle(
        color = Color.Black,
        fontWeight = FontWeight.Bold
    )

    val annotatedString = buildAnnotatedString {
        withStyle(regularStyle) {
            append("Enter the verification code sent to ")
        }
        withStyle(boldStyle) {
            append(phoneNumber)
        }
        withStyle(regularStyle) {
            append(" starting with ")
        }
        withStyle(boldStyle) {
            append(requestCode)
        }
    }

    Text(
        text = annotatedString,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}


fun String.formatInternational(): String {
    if (this.startsWith("0")) return this.replaceRange(0, 1, "233")

    return this
}