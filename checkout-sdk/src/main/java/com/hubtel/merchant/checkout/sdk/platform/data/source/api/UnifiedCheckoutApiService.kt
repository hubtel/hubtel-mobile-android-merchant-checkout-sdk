package com.hubtel.merchant.checkout.sdk.platform.data.source.api

import com.hubtel.merchant.checkout.sdk.network.ApiResult
import com.hubtel.merchant.checkout.sdk.network.createRetrofitService
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.CardReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.UserWalletReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.GhanaCardResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentChannelResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSEnrollResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.UserWalletResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface UnifiedCheckoutApiService {

    // Bank Card Endpoints
    @POST("/api/v1/merchant/{salesId}/cardnotpresentunified/initiate-authentication")
    suspend fun setup3DS(
        @Path("salesId") salesId: String,
        @Body req: ThreeDSSetupReq,
    ): DataResponse2<ThreeDSSetupInfo>

    @POST("/api/v1/merchant/{salesId}/cardnotpresentunified/authenticate-payer/{transactionId}")
    suspend fun enroll3DS(
        @Path("salesId") salesId: String,
        @Path("transactionId") transactionId: String,
    ): DataResponse2<ThreeDSEnrollResponse>

    // Mobile Money Endpoints
    @POST("/api/v1/merchant/{salesId}/unifiedcheckout/receive/mobilemoney/prompt")
    suspend fun receiveMoneyPrompt(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @POST("/api/v1/merchant/{salesId}/unifiedcheckout/receive/mobilemoney/directdebit")
    suspend fun mobileMoneyDirectDebit(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/preapprovalconfirm")
    suspend fun mobileMoneyPreapproval(
        @Path("salesId") salesId: String,
        @Query("Channel") channel: String?,
        @Query("CustomerMsisdn") customerMsisdn: String?,
        @Query("ClientReference") clientReference: String?,
    ): DataResponse2<CheckoutInfo>

    @POST("/api/v1/merchant/{salesId}/unifiedcheckout/preapprovalconfirm")
    suspend fun mobileMoneyPreapprovalNew(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/feecalculation")
    suspend fun getFees(
        @Path("salesId") salesId: String,
        @Query("Channel") channel: String?,
        @Query("Amount") amount: Double?,
    ): DataResponse2<CheckoutFee>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/statuscheck")
    suspend fun getTransactionStatus(
        @Path("salesId") salesId: String,
        @Query("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/wallets/{PhoneNumber}")
    suspend fun getWallets(
        @Path("salesId") salesId: String?,
        @Path("PhoneNumber") phoneNumber: String?,
    ): DataResponse<List<WalletResponse>>

    @POST("/api/v1/merchant/{salesId}/unifiedcheckout/verifyotp")
    suspend fun verifyOtp(
        @Path("salesId") salesId: String,
        @Body req: OtpReq,
    ): DataResponse2<OtpResponse>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/checkoutchannels")
    suspend fun getBusinessChannels(
        @Path("salesId") salesId: String
    ): DataResponse<PaymentChannelResponse>

    // /api/v1/merchant/\(salesId)/ghanacardkyc/addghanacard?PhoneNumber=\(mobileNumber)&CardID=\(idNumber)
    @GET("/api/v1/merchant/{salesId}/ghanacardkyc/addghanacard")
    suspend fun addGhanaCard(
        @Path("salesId") salesId: String?,
        @Query("PhoneNumber") phoneNumber: String?,
        @Query("CardID") cardId: String?,
    ): DataResponse2<GhanaCardResponse>

    @GET("/api/v1/merchant/{salesId}/ghanacardkyc/ghanacard-details/{PhoneNumber}")
    suspend fun getGhanaCardDetails(
        @Path("salesId") salesId: String?,
        @Path("PhoneNumber") phoneNumber: String?,
    ): DataResponse2<GhanaCardResponse>

    @GET("/api/v1/merchant/{salesId}/ghanacardkyc/confirm/{PhoneNumber}")
    suspend fun ghanaCardConfirm(
        @Path("salesId") salesId: String?,
        @Path("PhoneNumber") phoneNumber: String?,
    ): ApiResult<Any>

    @POST("/api/v1/merchant/{salesId}/ghanacardkyc/confirm-ghana-card")
    suspend fun ghanaCardConfirm2(
        @Path("salesId") salesId: String?,
        @Body req: CardReq
    ): ApiResult<Any>

    @POST("/api/v1/merchant/{salesId}/unifiedcheckout/addwallet")
    suspend fun addWallet(
        @Path("salesId") salesId: String?,
        @Body req: UserWalletReq
    ): DataResponse2<UserWalletResponse>

    companion object {
        operator fun invoke(apiKey: String): UnifiedCheckoutApiService {
            return createRetrofitService(
                baseUrl = "https://checkout.hubtel.com",
                apiKey = apiKey
            )
        }
    }
}


// https://checkout.hubtel.com/api/v1/merchant/11684/cardnotpresentunified/initiate-authentication
//Params: {amount: 1.0, cardHolderName: ,
//    cardNumber: 4445692000273218,
//    cvv: 371,
//    expiryMonth: 11,
//    expiryYear: 24,
//    customerMsisdn: 233200585542,
//    description: Camera,
//    clientReference: 33902619-b3c1-4372-945f-1256bc571355,
//    callbackUrl: https://webhook.site/80c682ba-abb2-4255-8942-c44d1ec766cc,
//     integrationChannel: UnifiedCheckout-Flutter
//    }
//
//
//
//    I  --> POST https://checkout.hubtel.com/api/v1/merchant/11684/cardnotpresentunified/initiate-authentication
//   I  {"amount":1.02,
//        "callbackUrl":"https://webhook.site/80c682ba-abb2-4255-8942-c44d1ec766cc",
//        "cardHolderName":"",
//        "cardNumber":"4445692000273218",
//        "clientReference":"36ac791e-a164-4801-ad6c-b941b56b7897"
//        ,"country":"gh",
//        "currency":"ghs",
//        "customerMsisdn":"233200585542",
//        "cvv":"371",
//        "description":"Rice with Coleslaw",
//        "expiryMonth":"11",
//        "expiryYear":"2024"
//    }