package com.hubtel.merchant.checkout.sdk.platform.data.source.api


import com.hubtel.merchant.checkout.sdk.network.createRetrofitService
import com.hubtel.merchant.checkout.sdk.network.response.DataListResponse
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.GetFeesReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.MobileMoneyCheckoutReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.OtpReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.request.ThreeDSSetupReq
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutFee
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.OtpResponse
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.ThreeDSSetupInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.TransactionStatusInfo
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.WalletResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CheckoutApiService {

    // 'https://checkout.hubtel.com/api/v1/merchant/11684/unifiedcheckout/statuscheck/YJKOOKKKJJJHH'

    @POST("/v2/merchantaccount/merchants/{salesId}/merchantcardnotpresent/setup-payerauth")
    suspend fun setup3DS(
        @Path("salesId") salesId: String,
        @Body req: ThreeDSSetupReq,
    ): DataResponse2<ThreeDSSetupInfo>

    @GET("/v2/merchantaccount/merchants/{salesId}/merchantcardnotpresent/enroll-payerauth/{transactionId}")
    suspend fun enroll3DS(
        @Path("salesId") salesId: String,
        @Path("transactionId") transactionId: String,
    ): DataResponse2<CheckoutInfo>

    @POST("/v2/merchantaccount/merchants/{salesId}/receive/mobilemoney")
    suspend fun receiveMobileMoney(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @POST("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/receive/mobilemoney/prompt")
    suspend fun receiveReceiveMoneyPrompt(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    @POST("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/receive/mobilemoney/directdebit")
    suspend fun receiveMobileMoneyDirectDebit(
        @Path("salesId") salesId: String,
        @Body req: MobileMoneyCheckoutReq,
    ): DataResponse2<CheckoutInfo>

    // https://checkout.hubtel.com/api/v1/merchant/11684/unifiedcheckout/preapprovalconfirm?Channel=mtn-gh-direct-debit&CustomerMsisdn=233249126761&ClientReference=test-flight-Last-12345
    @GET("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/preapprovalconfirm")
    suspend fun receiveMoneyPreapprovalConfirm(
        @Path("salesId") salesId: String,
        @Query("Channel") channel: String?,
        @Query("CustomerMsisdn") customerMsisdn: String?,
        @Query("ClientReference") clientReference: String?,
    ): DataResponse2<CheckoutInfo>


    @POST("/v2/merchantaccount/merchants/{salesId}/fees")
    suspend fun getFees(
        @Path("salesId") salesId: String,
        @Body req: GetFeesReq,
    ): DataResponse2<CheckoutFee>

    @GET("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/feecalculation")
    suspend fun getFeesDirectDebitCall(
        @Path("salesId") salesId: String,
        @Query("ChannelPassed") channel: String?,
        @Query("Amount") amount: Double?,
    ): DataResponse2<CheckoutFee>

    @GET("/v2/merchantaccount/merchants/{salesId}/statuscheck/{clientReference}")
    suspend fun getTransactionStatus(
        @Path("salesId") salesId: String,
        @Path("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/statuscheck/{clientReference}")
    suspend fun getTransactionStatusDirectDebit(
        @Path("salesId") salesId: String,
        @Path("clientReference") clientReference: String,
    ): DataResponse2<TransactionStatusInfo>

    @GET("https://checkout.hubtel.com/api/v1/merchant/{salesId}/unifiedcheckout/wallets/{PhoneNumber}")
    suspend fun getWallets(
        @Path("salesId") salesId: String?,
        @Path("PhoneNumber") phoneNumber: String?,
    ): DataListResponse<WalletResponse>

    @POST("https://checkout.hubtel.com/api/v1/merchant/2017766/unifiedcheckout/verifyotp")
    suspend fun verifyOtp(
        @Path("salesId") salesId: String,
        @Body req: OtpReq,
    ): DataResponse2<OtpResponse>

    @GET("/v2/merchantaccount/merchants/{salesId}/paymentchannels")
    suspend fun getBusinessPaymentChannels(
        @Path("salesId") salesId: String
    ): DataResponse2<List<String>>

    companion object {
        operator fun invoke(apiKey: String): CheckoutApiService {
            return createRetrofitService(
                baseUrl = "https://merchantcard-proxy.hubtel.com",
                apiKey = apiKey
            )
        }
    }

}