package com.hubtel.merchant.checkout.sdk.ux.pay.gh_card

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.UnifiedCheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.UnifiedCheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.UiState
import com.hubtel.merchant.checkout.sdk.ux.utils.update
import kotlinx.coroutines.launch

internal class GhCardVerificationViewModel constructor(private val unifiedCheckoutRepository: UnifiedCheckoutRepository) :
    ViewModel() {

    private val _cardUiState = mutableStateOf(UiState<GhanaCardResponse>())
    val cardUiState: State<UiState<GhanaCardResponse>> = _cardUiState

    fun addGhanaCard(config: CheckoutConfig, phoneNumber: String, id: String) {
        viewModelScope.launch {
            val result =
                unifiedCheckoutRepository.addGhanaCard(config.posSalesId ?: "", phoneNumber, id)

            _cardUiState.update { UiState(isLoading = true) }

            when (result) {
                is ApiResult.Success -> {
                    _cardUiState.update { UiState(isLoading = false, data = result.response.data, success = true) }
                }

                is ApiResult.HttpError -> {
                    _cardUiState.update {
                        UiState(
                            isLoading = false,
                            error = result.message ?: ""
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

                GhCardVerificationViewModel(checkoutRepository)
            }
        }
    }
}