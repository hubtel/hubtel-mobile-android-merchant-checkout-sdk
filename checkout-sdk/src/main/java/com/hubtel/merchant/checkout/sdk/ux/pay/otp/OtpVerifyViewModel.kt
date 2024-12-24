package com.hubtel.merchant.checkout.sdk.ux.pay.otp

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.UnifiedCheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.PaymentOtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpRequestResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.UnifiedCheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.UiState2
import com.hubtel.merchant.checkout.sdk.ux.model.UiText
import com.hubtel.merchant.checkout.sdk.ux.utils.update
import kotlinx.coroutines.launch

internal class OtpVerifyViewModel(private val unifiedCheckoutRepository: UnifiedCheckoutRepository) :
    ViewModel() {
    private val _otpUiState = mutableStateOf(UiState2<OtpResponse>())
    val otpUiState: State<UiState2<OtpResponse>> = _otpUiState

    private val _paymentOtpUiState = mutableStateOf(UiState2<OtpRequestResponse>())
    val paymentOtpUiState: State<UiState2<OtpRequestResponse>> = _paymentOtpUiState

    suspend fun verify(
        customerMsisdn: String,
        salesId: String,
        userOtpEntry: String,
        otpPrefix: String,
        otpRequestId: String,
        clientReference: String,
        preApprovalId: String,
        paymentChannel : String,
    ) {

        val req = PaymentOtpReq(
            customerMsisdn = customerMsisdn,
            requestId = otpRequestId,
            otpCode = "$otpPrefix-$userOtpEntry",
            clientReference = clientReference,
            hubtelPreApprovalId = preApprovalId,
        )

//        verifyPaymentOtp(salesId, req)

        val otpReq = OtpReq(
            customerMsisdn = customerMsisdn,
            otpCode = "$otpPrefix-$userOtpEntry",
            clientReferenceID = clientReference,
            hubtelPreApprovalID = preApprovalId,
            //TODO: BRIGHT PROMISED HE'LL FIX THIS
            channel =  "mtn-gh",
        )
        verifyOtp(salesId, otpReq)
    }


    private suspend fun verifyOtp(salesId: String, req: OtpReq) {
        _otpUiState.update { UiState2(isLoading = true) }

        val result = unifiedCheckoutRepository.verifyOtp(salesId, req)

        when (result) {
            is ApiResult.Success -> {
                _otpUiState.update {
                    UiState2(success = true, data = result.response.data)
                }
            }

            is ApiResult.HttpError -> {
                _otpUiState.update {
                    UiState2(success = false, error = UiText.DynamicString(result.message ?: ""))
                }
            }

            else -> {
                _otpUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
                    )
                }
            }
        }
    }

    private suspend fun verifyPaymentOtp(salesId: String, req: PaymentOtpReq) {
        _paymentOtpUiState.update { UiState2(isLoading = true) }

        val result = unifiedCheckoutRepository.verifyPaymentOtp(salesId, req)

        when (result) {
            is ApiResult.Success -> {
                _paymentOtpUiState.update {
                    UiState2(success = true, data = result.response.data)
                }
            }

            is ApiResult.HttpError -> {
                _paymentOtpUiState.update {
                    UiState2(success = false, error = UiText.DynamicString(result.message ?: ""))
                }
            }

            else -> {
                _paymentOtpUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
                    )
                }
            }
        }
    }

    companion object {
        fun getViewModelFactory(apiKey: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                val database = CheckoutDB.getInstance(application)
                val unifiedCheckoutService = UnifiedCheckoutApiService(apiKey ?: "")
                val checkoutPrefManager = CheckoutPrefManager(application)

                val checkoutRepository = UnifiedCheckoutRepository(
                    database, unifiedCheckoutService, checkoutPrefManager
                )

                OtpVerifyViewModel(checkoutRepository)
            }
        }
    }
}