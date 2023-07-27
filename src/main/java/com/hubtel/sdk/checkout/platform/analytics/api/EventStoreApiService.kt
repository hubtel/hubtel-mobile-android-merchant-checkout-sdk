package com.hubtel.core_analytics.api

import com.hubtel.sdk.checkout.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface EventStoreApiService {

    @JvmSuppressWildcards
    @POST("/api/v1/events")
    suspend fun postEvent(@Body data: Map<String, Any?>)

    companion object {

        @Volatile var hasToken: Boolean = false

        operator fun invoke(authToken: String?): EventStoreApiService {
            val httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $authToken")
                    .header("Referer", "checkout-sdk-android")
                    .build()
                chain.proceed(request)
            }

            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BODY
                }

                httpClient.addInterceptor(loggingInterceptor)
            }

            httpClient.readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)

            hasToken = authToken != null

            return Retrofit.Builder()
                .baseUrl("https://merchant-analytics-api.hubtel.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
                .create(EventStoreApiService::class.java)
        }
    }
}