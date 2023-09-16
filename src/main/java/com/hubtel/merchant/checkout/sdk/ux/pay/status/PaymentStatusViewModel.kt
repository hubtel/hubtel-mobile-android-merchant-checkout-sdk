package com.hubtel.merchant.checkout.sdk.ux.pay.status

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.core_ui.extensions.update
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.model.UiText
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.CheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import kotlinx.coroutines.launch

internal class PaymentStatusViewModel(
    private val checkoutRepository: CheckoutRepository
) : ViewModel() {

    private val _uiState = mutableStateOf(UiState2<TransactionStatusInfo>())
    val uiState: State<UiState2<TransactionStatusInfo>> = _uiState

    fun checkPaymentStatus(config: CheckoutConfig) {
        viewModelScope.launch {
            _uiState.update { UiState2(isLoading = true) }

            val result = checkoutRepository.getTransactionStatusDirectDebit(
                salesId = config.posSalesId ?: "",
                clientReference = config.clientReference ?: ""
            )

            if (result is ApiResult.Success) {
                _uiState.update {
                    UiState2(
                        data = result.response.data,
                        success = true
                    )
                }
            } else {
                _uiState.update {
                    UiState2(
                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
                    )
                }
            }
        }
    }

//    fun checkPaymentStatusDirectDebit(config: CheckoutConfig) {
//        viewModelScope.launch {
//            _uiState.update { UiState2(isLoading = true) }
//
////            val result = checkoutRepository.getTransactionStatus(
//            val result = checkoutRepository.getDirectTransactionStatus(
//                salesId = config.posSalesId ?: "",
//                clientReference = config.clientReference ?: ""
//            )
//
//            if (result is ApiResult.Success) {
//                _uiState.update {
//                    UiState2(
//                        data = result.response.data,
//                        success = true
//                    )
//                }
//            } else {
//                _uiState.update {
//                    UiState2(
//                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
//                    )
//                }
//            }
//        }
//    }

    companion object {
        fun getViewModelFactory(apiKey: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                val database = CheckoutDB.getInstance(application)
                val checkoutService = CheckoutApiService(apiKey ?: "")
                val checkoutPrefManager = CheckoutPrefManager(application)

                val checkoutRepository = CheckoutRepository(
                    database, checkoutService, checkoutPrefManager
                )
                PaymentStatusViewModel(checkoutRepository)
            }
        }

    }
}