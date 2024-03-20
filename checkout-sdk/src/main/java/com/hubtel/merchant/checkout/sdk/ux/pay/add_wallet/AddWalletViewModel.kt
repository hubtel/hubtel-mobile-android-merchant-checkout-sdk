package com.hubtel.merchant.checkout.sdk.ux.pay.add_wallet

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.UnifiedCheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.UserWalletReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.UserWalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.UnifiedCheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.model.UiState
import com.hubtel.merchant.checkout.sdk.ux.utils.update
import kotlinx.coroutines.launch

internal class AddWalletViewModel constructor(private val unifiedCheckoutRepository: UnifiedCheckoutRepository) :
    ViewModel() {

    private val _userWalletUiState = mutableStateOf(UiState<DataResponse2<UserWalletResponse>>())
    val userWalletUiState: State<UiState<DataResponse2<UserWalletResponse>>> = _userWalletUiState

    fun addUserWallet(config: CheckoutConfig, phoneNumber: String, provider: String) {
        viewModelScope.launch {
            val req = UserWalletReq(
                accountNo = phoneNumber,
                provider = provider,
                customerMobileNumber = config.msisdn ?: ""
            )
            val result = unifiedCheckoutRepository.addWallet(config.posSalesId ?: "", req)
            _userWalletUiState.update {
                UiState(isLoading = true)
            }

            when (result) {
                is ApiResult.Success -> {
                    _userWalletUiState.update {
                        UiState(isLoading = false, success = true, data = result.response)
                    }
                }

                is ApiResult.HttpError -> {
                    _userWalletUiState.update {
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

    fun resetUserWalletUiState() {
        _userWalletUiState.update {
            UiState()
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

                AddWalletViewModel(checkoutRepository)
            }
        }
    }
}