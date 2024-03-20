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
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
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

    fun verify(config: CheckoutConfig, checkoutInfo: CheckoutInfo, otpValue: String) {
        viewModelScope.launch {

            val req = OtpReq(
                checkoutInfo.customerMsisdn ?: "",
                checkoutInfo.hubtelPreapprovalId ?: "",
                config.clientReference ?: "",
                "${checkoutInfo.otpPrefix}-$otpValue"
            )
            verifyOtp(config, req)
        }
    }

    private suspend fun verifyOtp(config: CheckoutConfig, req: OtpReq) {
        _otpUiState.update { UiState2(isLoading = true) }

        val result = unifiedCheckoutRepository.verifyOtp(config.posSalesId ?: "", req)

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
                    UiState2(success = false, error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred))
                }
            }
        }
    }

    companion object {
        fun getViewModelFactory(apiKey: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

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