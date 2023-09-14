package com.hubtel.merchant.checkout.sdk.ux.pay.status.incorrect_pin

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.core_ui.model.UiState2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.CheckoutRepository
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager

internal class WrongPINScreenViewModel(private val checkoutRepository: CheckoutRepository): ViewModel() {
    private val _uiState = mutableStateOf(UiState2<TransactionStatusInfo>())
    val uiState: State<UiState2<TransactionStatusInfo>> = _uiState

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
                WrongPINScreenViewModel(checkoutRepository)
            }
        }
    }
}