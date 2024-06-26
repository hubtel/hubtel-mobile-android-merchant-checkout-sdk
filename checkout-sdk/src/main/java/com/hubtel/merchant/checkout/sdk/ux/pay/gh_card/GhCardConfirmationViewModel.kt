package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

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
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.CardReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.UnifiedCheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.UiState
import com.hubtel.merchant.checkout.sdk.ux.model.UiState2
import com.hubtel.merchant.checkout.sdk.ux.model.UiText
import com.hubtel.merchant.checkout.sdk.ux.utils.update
import kotlinx.coroutines.launch

internal class GhCardConfirmationViewModel constructor(private val unifiedCheckoutRepository: UnifiedCheckoutRepository) :
    ViewModel() {

    private val _ghanaCardUiState = mutableStateOf(UiState2<GhanaCardResponse>())
    val ghanaCardUiState: State<UiState2<GhanaCardResponse>> = _ghanaCardUiState

    private val _confirmUiState = mutableStateOf(UiState<Any>())
    val confirmUiState: State<UiState<Any>> = _confirmUiState

    fun getGhanaCardDetails(config: CheckoutConfig, phoneNumber: String) {
        viewModelScope.launch {

            val result = unifiedCheckoutRepository.getGhanaCardDetails(
                config.posSalesId ?: "", phoneNumber
            )
            _ghanaCardUiState.update {
                UiState2(isLoading = true)
            }

            when (result) {
                is ApiResult.Success -> {
                    _ghanaCardUiState.update {
                        UiState2(isLoading = false, data = result.response.data)
                    }
                }

                is ApiResult.HttpError -> {
                    _ghanaCardUiState.update {
                        UiState2(
                            success = false, error = UiText.DynamicString(result.message ?: "")
                        )
                    }
                }

                else -> {
                    _ghanaCardUiState.update {
                        UiState2(
                            success = false,
                            error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred),
                        )
                    }
                }
            }

        }
    }

    fun addGhanaCard(config: CheckoutConfig, phoneNumber: String, cardId: String) {
        viewModelScope.launch {
            val result =
                unifiedCheckoutRepository.addGhanaCard(config.posSalesId ?: "", phoneNumber, cardId)
            _ghanaCardUiState.update {
                UiState2(isLoading = true)
            }

            when (result) {
                is ApiResult.Success -> {
                    _ghanaCardUiState.update {
                        UiState2(
                            isLoading = false, data = result.response.data, success = true
                        )
                    }
                }

                is ApiResult.HttpError -> {
                    _ghanaCardUiState.update {
                        UiState2(
                            isLoading = false, error = UiText.DynamicString(result.message ?: "")
                        )
                    }
                }

                else -> {}
            }
        }
    }

    fun confirmGhanaCard(config: CheckoutConfig, phoneNumber: String) {
        viewModelScope.launch {
            val result = unifiedCheckoutRepository.ghanaCardConfirm(
                config.posSalesId ?: "", phoneNumber = phoneNumber ?: ""
            )
        }
    }

    fun confirmGhanaCard2(config: CheckoutConfig, req: CardReq?) {
        viewModelScope.launch {
            val result = unifiedCheckoutRepository.ghanaCardConfirm2(
                config.posSalesId ?: "", req ?: CardReq()
            )

            when (result) {
                is ApiResult.Success -> {
                    _confirmUiState.update {
                        UiState(isLoading = false, success = true)
                    }
                }

                is ApiResult.HttpError -> {
                    _confirmUiState.update {
                        UiState(
                            isLoading = false, error = result.message ?: ""
                        )
                    }
                }

                else -> {}
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

                GhCardConfirmationViewModel(checkoutRepository)
            }
        }
    }
}