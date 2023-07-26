package com.hubtel.sdk.checkout.ui.screens

//class CheckoutScreenViewModel(private val checkoutRepository: CheckoutRepository) : ViewModel() {
//    private val _userCardDetailsState =
//        mutableStateOf(UiState2<UserCardDetailsInfo>(isLoading = false))
//    val userCardDetailsState: State<UiState2<UserCardDetailsInfo>> = _userCardDetailsState
//
//    private val _cardEnrollmentState =
//        mutableStateOf(UiState2<Enroll3dsInfo>(isLoading = false))
//    val cardEnrollmentState: State<UiState2<Enroll3dsInfo>> = _cardEnrollmentState
//
//    var bankWallets by mutableStateOf(emptyList<Wallet>())
//        private set
//
//    private val transactionId: String?
//        get() = _userCardDetailsState.value.data?.transactionId
//    fun setup3DS(
//        amount: Int,
//        expiryMonth: String,
//        cardHolderName: String,
//        cvv: String,
//        expiryYear: String,
//        cardNumber: String,
//    ) {
//        viewModelScope.launch {
//            _userCardDetailsState.update{ UiState2(isLoading = true) }
//            val result = checkoutRepository.apiSetup3DS(
//                salesId = "11684",
//                UserCardDetailsReq(
//                    amount = amount,
//                    cardHolderName = cardHolderName,
//                    expiryMonth = expiryMonth,
//                    expiryYear = expiryYear,
//                    cvv = cvv,
//                    cardNumber = cardNumber,
//                    callbackUrl = "https://webhook.site/73db4705-d87a-4177-913b-ec42533f51c2",
//                    clientReference = "my_shop3212189000357",
//                    description = "Test Card Standalone",
//                    customerMsisdn = "233546465820"
//                )
//            )
//
//            when (result) {
//                is ApiResult.Success -> {
//                    _userCardDetailsState.value =
//                        UiState2(success = true, isLoading = false, data = result.response.data)
//                }
//
//                is ApiResult.HttpError -> {
//                    _userCardDetailsState.value = UiState2(
//                        success = false,
//                        isLoading = false,
//                        error = UiText.DynamicString(result.message ?: "")
//                    )
//                }
//
//                else -> {
//                    _userCardDetailsState.value = UiState2(
//                        success = false,
//                        isLoading = false,
//                        error = UiText.DynamicString(result.toString())
//                    )
//                }
//            }
//        }
//    }
//
//
//    fun enroll3DS(){
//        viewModelScope.launch {
//            _cardEnrollmentState.update{ UiState2(isLoading = true) }
//            val result = checkoutRepository.apiEnroll3DS(salesId = "11684",transactionId = transactionId)
//
//            when (result) {
//                is ApiResult.Success -> {
//                    _cardEnrollmentState.value =
//                        UiState2(success = true, isLoading = false, data = result.response.data)
//                }
//
//                is ApiResult.HttpError -> {
//                    _cardEnrollmentState.value = UiState2(
//                        success = false,
//                        isLoading = false,
//                        error = UiText.DynamicString(result.message ?: "")
//                    )
//                }
//
//                else -> {
//                    _cardEnrollmentState.value = UiState2(
//                        success = false,
//                        isLoading = false,
//                        error = UiText.DynamicString(result.toString())
//                    )
//                }
//            }
//        }
//    }
//
//    companion object {
//        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
//            @Suppress("UNCHECKED_CAST")
//            override fun <T : ViewModel> create(
//                modelClass: Class<T>,
//                extras: CreationExtras
//            ): T {
////                // Get the Application object from extras
////                val application = checkNotNull(extras[APPLICATION_KEY])
////                // Create a SavedStateHandle for this ViewModel from extras
////                val savedStateHandle = extras.createSavedStateHandle()
//
//                val apiKey = "T0UwajAzcjo5ZjAxMzhkOTk5ZmM0ODMxYjc3MWFhMzEzYTNjMThhNA=="
//                val checkoutService = CheckoutApiService(apiKey)
//
//                val checkoutRepository = CheckoutRepository(checkoutService)
//                return PayOrderViewModel(checkoutRepository) as T
//            }
//        }
//    }
//}