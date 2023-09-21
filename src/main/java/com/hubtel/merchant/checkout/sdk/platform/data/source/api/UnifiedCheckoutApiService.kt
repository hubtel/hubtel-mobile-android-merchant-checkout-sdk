package com.hubtel.merchant.checkout.sdk.platform.data.source.api

import com.hubtel.merchant.checkout.sdk.network.createRetrofitService
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.PaymentChannelResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface UnifiedCheckoutApiService {

    // Bank Card Endpoints
    @POST("/api/v1/merchant/{salesId}/cardnotpresent/setup-payerauth")
    suspend fun setup3DS(
        @Path("salesId") salesId: String,
        @Body req: ThreeDSSetupReq,
    ): DataResponse2<ThreeDSSetupInfo>

    @GET("/api/v1/merchant/{salesId}/cardnotpresent/enroll-payerauth/{transactionId}")
    suspend fun enroll3DS(
        @Path("salesId") salesId: String,
        @Path("transactionId") transactionId: String,
    ): DataResponse2<CheckoutInfo>

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

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/feecalculation")
    suspend fun getFeesDirectDebitCall(
        @Path("salesId") salesId: String,
        @Query("ChannelPassed") channel: String?,
        @Query("Amount") amount: Double?,
    ): DataResponse2<CheckoutFee>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/statuscheck/{clientReference}")
    suspend fun getTransactionStatusDirectDebit(
        @Path("salesId") salesId: String,
        @Path("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/wallets/{PhoneNumber}")
    suspend fun getWallets(
        @Path("salesId") salesId: String?,
        @Path("PhoneNumber") phoneNumber: String?,
    ): DataResponse<List<WalletResponse>>

    @POST("/api/v1/merchant/2017766/unifiedcheckout/verifyotp")
    suspend fun verifyOtp(
        @Path("salesId") salesId: String,
        @Body req: OtpReq,
    ): DataResponse2<OtpResponse>

    @GET("/api/v1/merchant/{salesId}/unifiedcheckout/checkoutchannels")
    suspend fun getBusinessChannels(
        @Path("salesId") salesId: String
    ): DataResponse<PaymentChannelResponse>

    companion object {
        operator fun invoke(apiKey: String): UnifiedCheckoutApiService {
            return createRetrofitService(
                baseUrl = "https://checkout.hubtel.com",
                apiKey = apiKey
            )
        }
    }
}