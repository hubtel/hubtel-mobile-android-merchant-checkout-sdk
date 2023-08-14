package com.hubtel.merchant.checkout.sdk.ux.pay.order


import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hubtel.core_ui.extensions.update
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.model.UiText
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.CheckoutRepository
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.utils.toWallet
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

internal class PayOrderViewModel constructor(
    private val checkoutRepository: CheckoutRepository,
) : ViewModel() {

    var bankWallets by mutableStateOf(emptyList<Wallet>())
        private set

    private val _paymentChannelsUiState = mutableStateOf(UiState2<List<PaymentChannel>>())
    val paymentChannelsUiState: State<UiState2<List<PaymentChannel>>> = _paymentChannelsUiState

    private val _checkoutFeesUiState = mutableStateOf(UiState2<List<CheckoutFee>>())
    val checkoutFeesUiState: State<UiState2<List<CheckoutFee>>> = _checkoutFeesUiState

    private val _threeDSSetupUiState = mutableStateOf(UiState2<ThreeDSSetupInfo>())
    val threeDSSetupUiState: State<UiState2<ThreeDSSetupInfo>> = _threeDSSetupUiState

    private val _checkoutUiState = mutableStateOf(UiState2<CheckoutInfo>())
    val checkoutUiState: State<UiState2<CheckoutInfo>> = _checkoutUiState

    var bankChannels by mutableStateOf<List<PaymentChannel>>(emptyList())
        private set

    var momoChannels by mutableStateOf<List<PaymentChannel>>(emptyList())
        private set

    var paymentInfo by mutableStateOf<PaymentInfo?>(null)
        private set

    var orderTotal by mutableStateOf(0.0)
        private set

    private val transactionId: String?
        get() = _threeDSSetupUiState.value.data?.transactionId

    private var loadServiceFeesJob: Job? = null

    fun initData(amount: Double) {
        getUserWallets()
        resetPaymentInfo()
        updateOrderTotal(amount)
    }

    private fun getUserWallets() {
        viewModelScope.launch {
            bankWallets = checkoutRepository.getWallets().map { it.toWallet() }
        }
    }

    fun updatePaymentInfo(
        payOrderWalletType: PayOrderWalletType,
        momoWalletUiState: MomoWalletUiState,
        bankCardUiState: BankCardUiState,
    ) {
        paymentInfo = when (payOrderWalletType) {
            PayOrderWalletType.MOBILE_MONEY -> {
                val walletProvider = momoWalletUiState.walletProvider

                PaymentInfo(
                    walletId = "0",
                    accountName = "",
                    accountNumber = momoWalletUiState.mobileNumber,
                    paymentType = payOrderWalletType.paymentTypeName,
                    providerName = walletProvider?.provider,
                    channel = walletProvider?.channelName,
                )
            }

            PayOrderWalletType.BANK_CARD -> {
                val useSavedCard = bankCardUiState.useSavedBankCard
                val bankWallet = if (useSavedCard) {
                    bankCardUiState.selectedWallet
                } else null

                val accountNumber = if (useSavedCard) {
                    bankWallet?.accountNumber
                } else bankCardUiState.cardNumber

                val expiryMonthYear = if (useSavedCard) {
                    bankWallet?.expiry
                } else bankCardUiState.monthYear.text

                val cvv = if (useSavedCard) bankWallet?.cvv else bankCardUiState.cvv

                val channel = when (accountNumber?.take(1)) {
                    "4" -> "cardnotpresent-visa"
                    "5" -> "cardnotpresent-master"
                    else -> "cardnotpresent"
                }

                PaymentInfo(
                    walletId = bankWallet?.id?.toString(),
                    accountNumber = accountNumber,
                    accountName = bankCardUiState.cardHolderName,
                    paymentType = payOrderWalletType.paymentTypeName,
                    providerName = bankWallet?.provider,
                    expiryMonthYear = expiryMonthYear,
                    cvv = cvv,
                    channel = channel,
                    saveForLater = bankCardUiState.saveForLater
                )
            }

        }.also {
            Timber.i("PaymentInfo: $it")
        }
    }

    private fun updateOrderTotal(amount: Double) {
        orderTotal = amount
    }

    private fun updateOrderTotal(amount: Double, fees: List<CheckoutFee>) {
        orderTotal = amount + (fees.sumOf { it.feeAmount ?: 0.0 })
    }

    private fun saveCard(paymentInfo: PaymentInfo) {
        checkoutRepository.saveCard(paymentInfo.toWallet())
    }

    fun getCheckoutFees(config: CheckoutConfig) {
        loadServiceFeesJob?.cancel()

        loadServiceFeesJob = viewModelScope.launch {
            getTransactionFees(config)

            val fees = _checkoutFeesUiState.value.data ?: emptyList()

            // update order total with fee amount added
            updateOrderTotal(config.amount, fees)
        }
    }

    private suspend fun getTransactionFees(config: CheckoutConfig) {
        delay(200) // debounce

        _checkoutFeesUiState.update { it.copy(isLoading = true) }

        val feesReq = GetFeesReq(
            amount = config.amount,
            channel = paymentInfo?.channel,
        )

        val result = checkoutRepository.getFees(config.posSalesId ?: "", feesReq)

        if (result is ApiResult.Success) {
            _checkoutFeesUiState.update {
                UiState2(
                    success = true,
                    data = result.response.data ?: emptyList()
                )
            }
        } else {
            _checkoutFeesUiState.update {
                UiState2(
                    success = false,
                    error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
                )
            }
        }
    }

    fun validateCardDetails(config: CheckoutConfig) {
        viewModelScope.launch {
            _threeDSSetupUiState.update {
                UiState2(isLoading = true)
            }

            val req = ThreeDSSetupReq(
                amount = orderTotal,
                cardHolderName = paymentInfo?.accountName?.trim(),
                cardNumber = paymentInfo?.accountNumber,
                expiryMonth = paymentInfo?.expiryMonth,
                expiryYear = paymentInfo?.fullExpiryYear,
                cvv = paymentInfo?.cvv,
                clientReference = config.clientReference,
                description = config.description,
                customerMsisdn = config.msisdn,
                callbackUrl = config.callbackUrl
            )

            val result = checkoutRepository.apiSetup3DS(config.posSalesId ?: "", req)

            when (result) {
                is ApiResult.Success -> {
                    _threeDSSetupUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data
                        )
                    }
                }

                is ApiResult.HttpError -> {
                    _threeDSSetupUiState.update {
                        UiState2(
                            success = false,
                            error = UiText.DynamicString(result.message ?: ""),
                        )
                    }
                }

                else -> {
                    _threeDSSetupUiState.update {
                        UiState2(
                            success = false,
                            error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred),
                        )
                    }
                }
            }
        }
    }

    fun payOrder(
        config: CheckoutConfig,
        walletType: PayOrderWalletType,
    ) {
        viewModelScope.launch {
            when (walletType) {
                PayOrderWalletType.BANK_CARD -> {
                    payOrderWithCard(config)
                }

                PayOrderWalletType.MOBILE_MONEY -> {
                    payOrderWithMomo(config)
                }
            }
        }
    }

    private suspend fun payOrderWithCard(config: CheckoutConfig) {
        _checkoutUiState.update { UiState2(isLoading = true) }

        val result = checkoutRepository.apiEnroll3DS(
            salesId = config.posSalesId ?: "",
            transactionId = transactionId ?: ""
        )

        when (result) {
            is ApiResult.Success -> {
                _checkoutUiState.update {
                    UiState2(
                        success = true,
                        data = result.response.data
                    )
                }
            }

            is ApiResult.HttpError -> {
                _checkoutUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.DynamicString(result.message ?: ""),
                    )
                }
            }

            else -> {
                _checkoutUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred),
                    )
                }
            }
        }

        //save card
        paymentInfo?.let {
            if (it.saveForLater) saveCard(it)
        }
    }

    private suspend fun payOrderWithMomo(
        config: CheckoutConfig,
    ) {
        _checkoutUiState.update { UiState2(isLoading = true) }

        val result = checkoutRepository.apiReceiveMobileMoney(
            salesId = config.posSalesId ?: "",
            req = MobileMoneyCheckoutReq(
                amount = orderTotal,
                channel = paymentInfo?.channel,
                clientReference = config.clientReference,
                customerMsisdn = paymentInfo?.accountNumber,
                customerName = "",
                description = config.description,
                primaryCallbackUrl = config.callbackUrl
            )
        )

        when (result) {
            is ApiResult.Success -> {
                _checkoutUiState.update {
                    UiState2(
                        success = true,
                        data = result.response.data
                    )
                }
            }

            is ApiResult.HttpError -> {
                _checkoutUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.DynamicString(result.message ?: ""),
                    )
                }
            }

            else -> {
                _checkoutUiState.update {
                    UiState2(
                        success = false,
                        error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred),
                    )
                }
            }
        }

        //save card
        paymentInfo?.let {
            if (it.saveForLater) saveCard(it)
        }
    }

    private fun resetPaymentInfo() {
        paymentInfo = null
    }

    fun getPaymentChannels(salesId: String?) {
        viewModelScope.launch {
            salesId ?: return@launch

            val savedChannels = checkoutRepository.getPaymentChannels().apply {
                bankChannels = this.getBankChannels()
                momoChannels = this.getMomoChannels()
            }

            _paymentChannelsUiState.update {
                UiState2(
                    isLoading = savedChannels.isEmpty(),
                    data = savedChannels,
                )
            }

            val result = checkoutRepository.getBusinessPaymentChannels(salesId)

            when (result) {
                is ApiResult.Success -> {
                    val resultChannels = result.response.data?.toPaymentChannels() ?: emptyList()

                    _paymentChannelsUiState.update {
                        it.copy(
                            data = resultChannels,
                            isLoading = false,
                        )
                    }

                    bankChannels = resultChannels.getBankChannels()
                    momoChannels = resultChannels.getMomoChannels()

                    checkoutRepository.savePaymentChannels(resultChannels)
                }

                is ApiResult.HttpError -> {}
                else -> {}
            }

        }
    }

    private fun List<PaymentChannel>.getBankChannels(): List<PaymentChannel> = filter { channel ->
        channel == PaymentChannel.MASTERCARD
                || channel == PaymentChannel.VISA
    }

    private fun List<PaymentChannel>.getMomoChannels(): List<PaymentChannel> = filter { channel ->
        channel == PaymentChannel.MTN
                || channel == PaymentChannel.VODAFONE
                || channel == PaymentChannel.AIRTEL_TIGO
    }


    companion object {

        fun getViewModelFactory(apiKey: String?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as Application

                val database = CheckoutDB.getInstance(application)
                val checkoutService = CheckoutApiService(apiKey ?: "")
                val checkoutPrefManager = CheckoutPrefManager(application)

                val checkoutRepository = CheckoutRepository(
                    database, checkoutService, checkoutPrefManager
                )

                PayOrderViewModel(checkoutRepository)
            }
        }

    }
}
