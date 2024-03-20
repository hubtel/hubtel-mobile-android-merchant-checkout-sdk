package com.hubtel.merchant.checkout.sdk.network

import com.hubtel.merchant.checkout.sdk.BuildConfig
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse
import com.hubtel.merchant.checkout.sdk.network.response.DataResponse2
import com.hubtel.merchant.checkout.sdk.platform.analytics.util.NetworkAnalyticsInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

internal inline fun <reified T> createRetrofitService(
    baseUrl: String,
    apiKey: String,
    timeoutInSeconds: Long = 60,
): T {
    val httpClient = OkHttpClient.Builder()

    httpClient.addInterceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Basic $apiKey")
            .build()
        chain.proceed(request)
    }

    httpClient.readTimeout(timeoutInSeconds, TimeUnit.SECONDS)
        .connectTimeout(timeoutInSeconds, TimeUnit.SECONDS)

    val networkAnalyticsInterceptor = NetworkAnalyticsInterceptor(timeoutInSeconds)

    httpClient.addInterceptor(networkAnalyticsInterceptor)

    if (BuildConfig.DEBUG) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        httpClient.addInterceptor(loggingInterceptor)
    }

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient.build())
        .build()
        .create(T::class.java)
}

internal typealias ResultWrapper2<T> = ApiResult<DataResponse2<T>>
internal typealias ResultWrapper<T> = ApiResult<DataResponse<T>>

sealed class ApiResult<out T : Any> {

    data class Success<out T : Any>(val response: T) : ApiResult<T>()

    data class HttpError(val code: Int?, val message: String?) : ApiResult<Nothing>()

    data class GenericError(val error: Exception) : ApiResult<Nothing>()

    object NoInternet : ApiResult<Nothing>()

    // Override for quick logging
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data=$response]"
            is HttpError -> "Http Error [httpCode=$code], [message=$message]"
            is GenericError -> "Error [error=${error.localizedMessage}]"
            NoInternet -> "No Internet"
        }
    }
}