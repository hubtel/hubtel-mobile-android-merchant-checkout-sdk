package com.hubtel.sdk.checkout.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.hubtel.core_network.ApiResult
import com.hubtel.core_network.model.requests.checkout.CheckoutWallet
import com.hubtel.core_network.model.requests.checkout.EcomCheckoutReq
import com.hubtel.core_network.model.requests.checkout.EventTicketCheckoutReq
import com.hubtel.core_network.model.requests.checkout.EventsOrderItemReq
import com.hubtel.core_network.model.requests.checkout.InstantServicesCheckoutReq
import com.hubtel.core_network.model.requests.checkout.InsuranceCheckoutRequest
import com.hubtel.core_network.model.requests.checkout.InsuranceOrderItem
import com.hubtel.core_network.model.requests.checkout.PaymentExtras
import com.hubtel.core_network.model.requests.checkout.SMSCheckoutReq
import com.hubtel.core_network.model.requests.checkout.TicketOrderReq
import com.hubtel.core_network.model.requests.instant_services.GetSendMoneyFeesReq
import com.hubtel.core_network.repository.AccountRepository
import com.hubtel.core_network.repository.CheckoutRepository
import com.hubtel.core_network.service.CheckoutApiService
import com.hubtel.core_storage.pref.manager.AppPrefManager
import com.hubtel.core_storage.model.Wallet
import com.hubtel.core_storage.model.WalletType
import com.hubtel.core_storage.model.checkout.CheckoutFee
import com.hubtel.core_storage.model.checkout.CheckoutInfo
import com.hubtel.core_storage.model.checkout.ThreeDSSetupInfo
import com.hubtel.core_storage.model.ecom.DeliveryAddress2
import com.hubtel.core_ui.extensions.update
import com.hubtel.core_ui.model.UiState2
import com.hubtel.core_ui.model.UiText
import com.hubtel.core_ui.shared.BaseAppNavigator
import com.hubtel.feature_checkout.R
import com.hubtel.feature_checkout.model.CheckoutConfig
import com.hubtel.feature_checkout.model.CheckoutMode
import com.hubtel.feature_wallet.extensions.channelName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject


class PayOrderViewModel (
    private val checkoutRepository: CheckoutRepository,
) : ViewModel() {

    var momoWallets by mutableStateOf(emptyList<Wallet>())
        private set

    var bankWallets by mutableStateOf(emptyList<Wallet>())
        private set

    var hubtelWallet by mutableStateOf<Wallet?>(null)
        private set

    private val _checkoutFeesUiState = mutableStateOf(UiState2<List<CheckoutFee>>())
    val checkoutFeesUiState: State<UiState2<List<CheckoutFee>>> = _checkoutFeesUiState

    private val _threeDSSetupUiState = mutableStateOf(UiState2<ThreeDSSetupInfo>())
    val threeDSSetupUiState: State<UiState2<ThreeDSSetupInfo>> = _threeDSSetupUiState

    private val _checkoutUiState = mutableStateOf(UiState2<CheckoutInfo>())
    val checkoutUiState: State<UiState2<CheckoutInfo>> = _checkoutUiState

    var paymentInfo by mutableStateOf<PaymentInfo?>(null)
        private set

    var orderTotal by mutableStateOf(0.0)
        private set

    private val referenceId3ds: String?
        get() = _threeDSSetupUiState.value.data?.referenceId

    private val clientReference: String?
        get() = _threeDSSetupUiState.value.data?.hubtelReference

    private var loadServiceFeesJob: Job? = null

    fun initData(amount: Double) {
        getCustomerProfile()
        getUserWallets()
        resetPaymentInfo()
        updateOrderTotal(amount)
    }

    private fun getCustomerProfile() {
        viewModelScope.launch {
            accountRepository.getUserProfile()
        }
    }

    private fun getUserWallets() {
        viewModelScope.launch {
            val userPhoneNumber = appPrefManager.phoneNumber

            accountRepository.getUserWalletsAsFlow()
                .collectLatest { wallets ->
                    val momoW = wallets.filter { it.walletType == WalletType.Momo }
                    val bankW = wallets.filter { it.walletType == WalletType.Card }
                    val hubtelW = wallets.find {
                        it.walletType == WalletType.Hubtel
                                || it.walletType == WalletType.Gratis
                    }

                    val isAccountInMomoWallets = momoW.find {
                        userPhoneNumber.contains(it.accountNumber?.take(9) ?: "")
                    } != null

                    bankWallets = bankW
                    hubtelWallet = hubtelW
                    momoWallets = if (isAccountInMomoWallets) momoW else {
                        val defaultMomoWallet = Wallet(
                            id = "0",
                            customerID = null,
                            accountName = null,
                            accountNumber = userPhoneNumber,
                            type = "MOMO",
                            providerID = "62001",
                            provider = "mtn",
                            providerType = "TELCO",
                            status = "**/**",
                            expiry = null,
                            currentBalance = null,
                            availableBalance = null,
                            secret = null,
                            countryCode = null,
                            walletImageUrl = null,
                            hasGateKeeperPass = null,
                            createdAt = null,
                            updateAt = null,
                        )

                        momoW + defaultMomoWallet
                    }
                }
        }
    }

    fun updatePaymentInfo(
        payOrderWalletType: PayOrderWalletType,
        momoWalletUiState: MomoWalletUiState,
        bankCardUiState: BankCardUiState,
        hubtelWallet: Wallet?,
    ) {
        paymentInfo = when (payOrderWalletType) {
            PayOrderWalletType.MOBILE_MONEY -> {
                val momoWallet = momoWalletUiState.selectedWallet
                val walletProvider = momoWalletUiState.walletProvider

                PaymentInfo(
                    walletId = momoWallet?.id?.toIntOrNull() ?: 0,
                    accountNumber = momoWallet?.accountNumber,
                    paymentType = payOrderWalletType.paymentTypeName,
                    providerName = walletProvider?.provider,
                    channel = walletProvider?.channelName
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

                val cvv = if (useSavedCard) {
                    bankWallet?.secret
                } else bankCardUiState.cvv

                val channel = when (accountNumber?.take(1)) {
                    "4" -> "cardnotpresent-visa"
                    "5" -> "cardnotpresent-master"
                    else -> "cardnotpresent"
                }

                PaymentInfo(
                    walletId = bankWallet?.id?.toIntOrNull() ?: 0,
                    accountNumber = accountNumber,
                    paymentType = payOrderWalletType.paymentTypeName,
                    providerName = bankWallet?.provider,
                    expiryMonthYear = expiryMonthYear,
                    cvv = cvv,
                    channel = channel,
                    saveForLater = bankCardUiState.saveForLater
                )
            }

            PayOrderWalletType.HUBTEL_BALANCE -> {
                PaymentInfo(
                    walletId = hubtelWallet?.id?.toIntOrNull(),
                    accountNumber = hubtelWallet?.accountNumber,
                    paymentType = payOrderWalletType.paymentTypeName,
                    channel = hubtelWallet?.channelName
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
        accountRepository.saveUserWallet(listOf(paymentInfo.toWallet()))
    }

    fun getCheckoutFees(config: CheckoutConfig) {
        loadServiceFeesJob?.cancel()

        loadServiceFeesJob = viewModelScope.launch {
            when (config.checkoutMode) {
                CheckoutMode.SEND_MONEY -> {
                    getSendMoneyFees(config)
                }

                else -> {
                    // sets fees ui state to success (already retrieved)
                    _checkoutFeesUiState.update {
                        UiState2(
                            success = true,
                            data = emptyList()
                        )
                    }
                }
            }

            // update order total with fee amount added
            updateOrderTotal(config.amount, checkoutFeesUiState.value.data ?: emptyList())
        }
    }

    private suspend fun getSendMoneyFees(config: CheckoutConfig) {
        delay(200) // debounce

        _checkoutFeesUiState.update { it.copy(isLoading = true) }

        Timber.i("PaymentInfo: $paymentInfo")

        val feesReq = GetSendMoneyFeesReq(
            amount = config.amount,
            channel = paymentInfo?.channel,
            serviceId = config.serviceCheckout?.serviceId,
            walletAccountNumber = paymentInfo?.accountNumber,
            receiverAccountNumber = config.serviceCheckout?.serviceType,
        )

        val result = checkoutRepository.getSendMoneyCheckoutFees(feesReq)

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
                    error = UiText.StringResource(R.string.sorry_an_error_occurred)
                )
            }
        }
    }

    fun validateCardDetails(config: CheckoutConfig) {
        viewModelScope.launch {
            _threeDSSetupUiState.update {
                UiState2(isLoading = true)
            }

            val result = checkoutRepository.verifyCardValidity(
                cardNumber = paymentInfo?.accountNumber ?: "",
                expiryMonth = paymentInfo?.expiryMonth,
                expiryYear = paymentInfo?.fullExpiryYear,
            )


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
                            error = UiText.StringResource(R.string.something_went_wrong),
                        )
                    }
                }
            }
        }
    }

    fun payOrder(config: CheckoutConfig) {
        _checkoutUiState.update { UiState2(isLoading = true) }

        when (config.checkoutMode) {
            CheckoutMode.SEND_MONEY,
            CheckoutMode.ADB -> {
                instantServiceCheckout(config)
            }

            CheckoutMode.E_COMMERCE -> {
                ecommerceCheckout(config)
            }

            CheckoutMode.SMS_AND_MONEY -> {
                smsAndMoneyCheckout(config)
            }

            CheckoutMode.OTHER_SERVICES -> {
                instantServiceCheckoutLegacy(config)
            }

            CheckoutMode.INSURANCE -> {
                insuranceCheckout(config)
            }

            CheckoutMode.EVENT_TICKET -> {
                eventTicketCheckout(config)
            }
        }

        //save card
        paymentInfo?.let {
            if (it.saveForLater) saveCard(it)
        }
    }

    private fun instantServiceCheckout(config: CheckoutConfig) {
        viewModelScope.launch {

            val checkoutRequest = InstantServicesCheckoutReq(
                sessionId = config.serviceCheckout?.sessionId,
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                fcm = appPrefManager.fcmPlayerId,
                hasElevyUpdate = false,
                clientReference = clientReference,
                note = config.serviceCheckout?.serviceName,
            )

            val result = checkoutRepository.instantServiceCheckout(checkoutRequest)

            when (result) {
                is ApiResult.Success -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data,
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
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun instantServiceCheckoutLegacy(config: CheckoutConfig) {
        viewModelScope.launch {

            val checkoutRequest = InstantServicesCheckoutReq(
                sessionId = config.otherServices?.sessionId,
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                fcm = appPrefManager.fcmPlayerId,
                hasElevyUpdate = false,
                note = "",
                branchId = config.otherServices?.branchId,
                serviceId = config.otherServices?.serviceId,
                businessId = config.otherServices?.businessId,
                clientReference = clientReference
            )

            val result = checkoutRepository.instantServiceCheckoutLegacy(checkoutRequest)

            when (result) {
                is ApiResult.Success -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data,
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
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun ecommerceCheckout(config: CheckoutConfig) {
        viewModelScope.launch {

            val ecomCheckoutConfig = config.ecomCheckout

            val deliveryAddress = DeliveryAddress2(
                name = ecomCheckoutConfig?.addressName,
                address = ecomCheckoutConfig?.addressName,
                latitude = ecomCheckoutConfig?.latitude.toString(),
                longitude = ecomCheckoutConfig?.longitude?.toString(),
                mobileNumber = appPrefManager.customer?.mobileNumber,
                isDefault = false,
                id = null
            )

            val cartCheckoutReq = EcomCheckoutReq(
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                couponIds = config.ecomCheckout?.couponId
                    ?.let { listOf(it) },
                deliveryAddress = deliveryAddress,
                cartType = ecomCheckoutConfig?.cartTypeName,
                isBnpl = false, // todo verify
                fcm = appPrefManager.fcmPlayerId,
                note = ecomCheckoutConfig?.deliveryNote,
                country = "GH",
                zone = ecomCheckoutConfig?.zone,
                station = ecomCheckoutConfig?.station,
                city = ecomCheckoutConfig?.city,
                region = null,
                latitude = ecomCheckoutConfig?.latitude?.toString(),
                longitude = ecomCheckoutConfig?.longitude?.toString(),
                tipAmount = ecomCheckoutConfig?.tipAmount,
                isScheduledOrder = ecomCheckoutConfig?.isScheduledOrder,
                scheduledStartDateTime = ecomCheckoutConfig?.scheduledStartDateTime?.let { it.ifBlank { null } },
                scheduledEndDateTime = ecomCheckoutConfig?.scheduledEndDateTime?.let { it.ifBlank { null } },
                hubtelReference = clientReference,
            )

            val result = checkoutRepository.checkoutEcomOrder(cartCheckoutReq)

            when (result) {
                is ApiResult.Success -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data,
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
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun smsAndMoneyCheckout(config: CheckoutConfig) {
        viewModelScope.launch {

            val checkoutRequest = SMSCheckoutReq(
                messageDetailKey = config.smsCheckout?.messageDetailKey,
                orderNumber = config.smsCheckout?.messageDetailKey,
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                fcm = appPrefManager.fcmPlayerId,
                note = config.smsCheckout?.note,
                clientReference = clientReference,
                country = "GH",
                zone = null,
                station = null,
                city = null,
                region = null,
                latitude = null,
                longitude = null,
            )

            val result = checkoutRepository.smsAndMoneyCheckout(checkoutRequest)

            when (result) {
                is ApiResult.Success -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data,
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
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun eventTicketCheckout(config: CheckoutConfig) {
        viewModelScope.launch {

            val eventConfig = config.eventCheckout

            val checkoutRequest = EventTicketCheckoutReq(
                id = UUID.randomUUID().toString(), // todo remove id to backend
                orderNumber = UUID.randomUUID().toString(), // todo remove id to backend
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                fcm = appPrefManager.fcmPlayerId,
                note = config.serviceCheckout?.serviceName,
                hasElevyUpdate = false,
                eventsOrderItem = EventsOrderItemReq(
                    eventId = eventConfig?.eventId,
                    startDate = eventConfig?.eventStartDate,
                    endDate = eventConfig?.eventEndDate,
                    tickets = eventConfig?.tickets?.map {
                        TicketOrderReq(
                            id = it.ticketId,
                            quantity = it.quantity
                        )
                    }
                ),
                clientReference = clientReference,
                city = null,
                country = "GH",
            )

            val result = checkoutRepository.eventTicketCheckout(checkoutRequest)

            when (result) {
                is ApiResult.Success -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = true,
                            data = result.response.data,
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
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun insuranceCheckout(config: CheckoutConfig) {
        viewModelScope.launch {
            _checkoutUiState.update { UiState2(isLoading = true) }

            val checkoutRequest = InsuranceCheckoutRequest(
                quoteId = config.insuranceCheckout?.quoteId,
                wallet = CheckoutWallet(
                    id = paymentInfo?.walletId,
                    accountNo = paymentInfo?.accountNumber,
                    provider = paymentInfo?.providerName,
                    referenceId3Ds = referenceId3ds,
                    saveWalletOnAccount = paymentInfo?.saveForLater,
                ),
                paymentExtras = PaymentExtras(
                    middle = paymentInfo?.middle,
                    expiry = paymentInfo?.expiryMonthYear,
                    cvv = paymentInfo?.cvv,
                    cardNumber = paymentInfo?.accountNumber,
                    expiryMonth = paymentInfo?.expiryMonth,
                    expiryYear = paymentInfo?.fullExpiryYear,
                ),
                paymentType = paymentInfo?.paymentType,
                fcm = appPrefManager.fcmPlayerId,
                note = config.insuranceCheckout?.quoteId,
                clientReference = clientReference,
                insuranceOrderItem = InsuranceOrderItem(
                    make = config.insuranceCheckout?.make,
                    model = config.insuranceCheckout?.model,
                    color = config.insuranceCheckout?.color,
                    yearManufactured = config.insuranceCheckout?.yearManufactured,
                    engineCapacity = config.insuranceCheckout?.engineCapacity,
                    coverType = config.insuranceCheckout?.coverType,
                    riskId = config.insuranceCheckout?.riskId,
                    registrationNumber = config.insuranceCheckout?.registrationNumber,
                    quoteEstId = config.insuranceCheckout?.quoteEstId,
                    chassisNumber = config.insuranceCheckout?.chassisNumber,
                    modelNo = config.insuranceCheckout?.modelNo,
                    description = config.insuranceCheckout?.description,
                    noOfSeats = config.insuranceCheckout?.noOfSeats,
                    coverPeriod = config.insuranceCheckout?.coverPeriod,
                    vehRegYear = config.insuranceCheckout?.vehRegYear,
                    bodyType = config.insuranceCheckout?.bodyType
                ),
                city = null,
                region = null,
                country = null,
                zone = null,
                latitude = null,
                longitude = null,
                station = null,
                description = null,
                discountAmount = 0.0,
                orderNumber = null
            )

            val result = checkoutRepository.insuranceCheckout(checkoutRequest)

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
                            error = UiText.DynamicString(result.message ?: "")
                        )
                    }
                }

                else -> {
                    _checkoutUiState.update {
                        UiState2(
                            success = false,
                            error = UiText.StringResource(R.string.sorry_an_error_occurred)
                        )
                    }
                }
            }
        }
    }

    private fun resetPaymentInfo() {
        paymentInfo = null
    }

    fun addMomoWallet() {
        appNavigator.openAddMomoWallet()
    }
}


//
//companion object {
//
//    val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//        @Suppress("UNCHECKED_CAST")
//        override fun <T : ViewModel> create(
//            modelClass: Class<T>,
//            extras: CreationExtras
//        ): T {
//
//            val apiKey = "T0UwajAzcjo5ZjAxMzhkOTk5ZmM0ODMxYjc3MWFhMzEzYTNjMThhNA=="
//            val checkoutService = CheckoutApiService(apiKey)
//
//            val checkoutRepository = CheckoutRepository(checkoutService)
//            return PayOrderViewModel(checkoutRepository) as T
//        }
//    }
//}