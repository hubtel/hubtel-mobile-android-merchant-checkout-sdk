package com.hubtel.merchant.checkout.sdk.network.repository

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hubtel.merchant.checkout.sdk.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

internal abstract class Repository {
    suspend fun <T : Any> makeRequestToApi(call: suspend () -> T): ApiResult<T> {
        val result = try {
            val data = withContext(Dispatchers.IO) {
                call.invoke()
            }

            ApiResult.Success(data)
        } catch (throwable: Exception) {
            return when (throwable) {
                is HttpException -> {
                    ApiResult.HttpError(throwable.code(), parseErrorMessage(throwable))
                }

                is IOException -> ApiResult.NoInternet
                else -> ApiResult.GenericError(throwable)
            }
        }

        return result
    }

    private fun parseErrorMessage(httpException: HttpException): String {
        val errorBody = httpException.response()?.errorBody()?.string()

        return try {
            val messageObject = Gson().fromJson(errorBody, JsonObject::class.java)
            (messageObject.get("developer_message") ?: messageObject.get("message")).asString
        } catch (ex: Exception) {
            httpException.message()
        }
    }

}