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
import com.hubtel.merchant.checkout.sdk.network.ResultWrapper
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.CheckoutApiService
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutType
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentChannelResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.db.CheckoutDB
import com.hubtel.merchant.checkout.sdk.platform.data.source.repository.CheckoutRepository
import com.hubtel.merchant.checkout.sdk.platform.model.Wallet
import com.hubtel.merchant.checkout.sdk.storage.CheckoutPrefManager
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.utils.toWallet
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

internal class PayOrderViewModel constructor(
    private val checkoutRepository: CheckoutRepository,
) : ViewModel() {

    var bankWallets by mutableStateOf(emptyList<Wallet>())
        private set

    private var _customerWalletsUiState = mutableStateOf(UiState2<List<WalletResponse>>())
    val customerWalletsUiState: State<UiState2<List<WalletResponse>>> = _customerWalletsUiState

    private val _paymentChannelsUiState = mutableStateOf(UiState2<List<PaymentChannel>>())
    val paymentChannelsUiState: State<UiState2<List<PaymentChannel>>> = _paymentChannelsUiState

    private val _businessInfoUiState = mutableStateOf(UiState2<BusinessResponseInfo>())
    val businessInfoUiState: State<UiState2<BusinessResponseInfo>> = _businessInfoUiState

    private val _checkoutFeesUiState = mutableStateOf(UiState2<CheckoutFee>())
    val checkoutFeesUiState: State<UiState2<CheckoutFee>> = _checkoutFeesUiState

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

    fun getCustomerWallets(config: CheckoutConfig) {
        viewModelScope.launch {
            val result = checkoutRepository.getCustomerWallets(config.posSalesId, config.msisdn)

            _customerWalletsUiState.update {
                UiState2(isLoading = true)
            }
            when (result) {
                is ApiResult.Success -> {
                    _customerWalletsUiState.update {
                        UiState2(isLoading = false, data = result.response.data)
                    }
                }

                is ApiResult.HttpError -> {
                    _customerWalletsUiState.update {
                        UiState2(
                            isLoading = false,
                            data = null,
                            error = UiText.DynamicString(result.message ?: "")
                        )
                    }
                }

                else -> {
                    _customerWalletsUiState.update {
                        UiState2(
                            success = false,
                            error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred),
                        )
                    }
                }
            }
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
                    "5" -> "cardnotpresent-master" // TODO: may need to change to cardnotpresent-mastercard
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

//            PayOrderWalletType.OTHERS -> {
//                val provider = otherPaymentUiState.paymentProvider
//
//                PaymentInfo(
//                    walletId = "",
//                    accountName = "",
//                    accountNumber = otherPaymentUiState.number,
//                    paymentType = payOrderWalletType.paymentTypeName,
//                    providerName = provider?.provider,
//                    channel = provider?.name // TODO: replace
//                )
//            }
        }.also {
            Timber.i("PaymentInfo: $it")
        }
    }

    private fun updateOrderTotal(amount: Double) {
        orderTotal = amount
    }

    private fun updateOrderTotal(amount: Double, fees: List<CheckoutFee>) {
        orderTotal = /*amount*/ +(fees.sumOf { it.amountPayable ?: 0.0 })
    }

    private fun saveCard(paymentInfo: PaymentInfo) {
        checkoutRepository.saveCard(paymentInfo.toWallet())
    }

    fun getCheckoutFees(config: CheckoutConfig) {
        loadServiceFeesJob?.cancel()

        loadServiceFeesJob = viewModelScope.launch {
            getTransactionFees(config)

            val fee = _checkoutFeesUiState.value.data ?: CheckoutFee(
                0.0,
                0.0,
                CheckoutType.RECEIVE_MONEY_PROMPT.rawValue,
                0.0,
            )

            // update order total with fee amount added
            updateOrderTotal(config.amount, listOf(fee))
        }
    }

    private suspend fun getTransactionFees(config: CheckoutConfig) {
        delay(200) // debounce

        _checkoutFeesUiState.update { it.copy(isLoading = true) }

        val feesReq = GetFeesReq(
            amount = config.amount,
            channel = paymentInfo?.channel,
        )

        val result = checkoutRepository.getFeesDirectDebit(config.posSalesId ?: "", feesReq)

        if (result is ApiResult.Success) {

            _checkoutFeesUiState.update {
                UiState2(
                    success = true,
                    data = result.response.data ?: CheckoutFee(
                        0.0,
                        0.0,
                        CheckoutType.RECEIVE_MONEY_PROMPT.rawValue,
                        0.0
                    )
                )
            }

            Timber.d("Checkout data", _checkoutFeesUiState.value.data)
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

        val feesReq = GetFeesReq(
            amount = config.amount,
            channel = paymentInfo?.channel,
        )
        val checkoutTypeResult =
            checkoutRepository.getFeesDirectDebit(config.posSalesId ?: "", feesReq)

        if (checkoutTypeResult is ApiResult.Success) {
            val type =
                checkoutTypeResult.response.data?.getCheckoutType ?: CheckoutType.DIRECT_DEBIT

            when (type) {
                CheckoutType.RECEIVE_MONEY_PROMPT -> {
                    val result = checkoutRepository.apiReceiveMobileMoney(
                        salesId = config.posSalesId ?: "",
                        req = MobileMoneyCheckoutReq(
                            amount = config.amount,
                            channel = if (paymentInfo?.channel?.startsWith("mtn") != true) paymentInfo?.channel else "mtn-gh",
//                            channel = "mtn-gh-direct-debit",
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
                }

                CheckoutType.DIRECT_DEBIT -> {
                    val result = checkoutRepository.apiReceiveMobileMoneyDirectDebit(
                        salesId = config.posSalesId ?: "",
                        req = MobileMoneyCheckoutReq(
                            amount = config.amount,
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
                }

                CheckoutType.PRE_APPROVAL_CONFIRM -> {
                    val result = checkoutRepository.apiReceiveMoneyPreapproval(
                        salesId = config.posSalesId ?: "",
                        req = MobileMoneyCheckoutReq(
                            amount = config.amount,
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

            val result = checkoutRepository.getBusinessPaymentChannelsNew(salesId)

            when (result) {
                is ApiResult.Success -> {
                    val resultChannels =
                        result.response.data?.channels?.toPaymentChannels() ?: emptyList()

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

    private suspend fun fetchData(config: CheckoutConfig): Pair<ResultWrapper<List<WalletResponse>>, ResultWrapper<PaymentChannelResponse>> =
        coroutineScope {
            val customerWalletsDeferred = async {
                checkoutRepository.getCustomerWallets(config.posSalesId, config.msisdn)
            }
            val paymentChannelsDeferred = async {
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


//            checkoutRepository.getBusinessPaymentChannels(config.posSalesId ?: "")
                checkoutRepository.getBusinessPaymentChannelsNew(config.posSalesId ?: "")

            }

            val customerWalletsResult = customerWalletsDeferred.await()
            val paymentChannelsResult = paymentChannelsDeferred.await()

            customerWalletsResult to paymentChannelsResult
        }

    fun getCustomerWalletsAndPaymentChannels(config: CheckoutConfig) {
        viewModelScope.launch {
            _customerWalletsUiState.update { UiState2(isLoading = true) }
            _paymentChannelsUiState.update { UiState2(isLoading = true) }
            _businessInfoUiState.update { UiState2(isLoading = true) }

            val (customerWalletsResult, paymentChannelsResult) = fetchData(config)

            // Handle customerWalletsResult
            _customerWalletsUiState.update {
                when (customerWalletsResult) {
                    is ApiResult.Success -> {
                        UiState2(isLoading = false, data = customerWalletsResult.response.data)
                    }

                    is ApiResult.HttpError -> {
                        UiState2(
                            isLoading = false,
                            data = null,
                            error = UiText.DynamicString(customerWalletsResult.message ?: "")
                        )
                    }

                    else -> {
                        UiState2(
                            success = false,
                            error = UiText.StringResource(R.string.checkout_sorry_an_error_occurred)
                        )
                    }
                }
            }

            // Handle paymentChannelsResult & Business info
            when (paymentChannelsResult) {
                is ApiResult.Success -> {

                    // payment channels
                    val resultChannels =
                        paymentChannelsResult.response.data?.channels?.toPaymentChannels()
                            ?: emptyList()

                    _paymentChannelsUiState.update {
                        it.copy(
                            data = resultChannels,
                            isLoading = false,
                        )
                    }

                    bankChannels = resultChannels.getBankChannels()
                    momoChannels = resultChannels.getMomoChannels()

                    checkoutRepository.savePaymentChannels(resultChannels)

                    // business info
                    val businessInfo = BusinessResponseInfo(
                        businessID = paymentChannelsResult.response.data?.businessID,
                        businessName = paymentChannelsResult.response.data?.businessName,
                        businessLogoURL = paymentChannelsResult.response.data?.businessLogoURL
                    )

                    _businessInfoUiState.update {
                        UiState2(isLoading = false, data = businessInfo)
                    }

                }

                is ApiResult.HttpError -> {
                    // Handle HTTP error
                }

                else -> {
                    // Handle other errors
                }
            }
        }
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

