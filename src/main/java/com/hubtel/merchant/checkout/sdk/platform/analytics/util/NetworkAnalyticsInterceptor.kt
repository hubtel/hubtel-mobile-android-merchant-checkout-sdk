package com.hubtel.merchant.checkout.sdk.platform.analytics.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.ApiRequestEvent
import com.hubtel.merchant.checkout.sdk.platform.analytics.events.types.Status
import com.hubtel.merchant.checkout.sdk.platform.analytics.recordApiRequestEvent
import okhttp3.Interceptor
import okhttp3.Response
import okio.GzipSource
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class NetworkAnalyticsInterceptor(
    private val timeoutInSeconds: Long,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val urlString = request.url.toString()
        val startNanoSecond = System.nanoTime()

        var response: Response? = null
        var requestError: Exception? = null

        try {
            response = chain.proceed(request)
        } catch (ex: Exception) {
            requestError = ex
        }

        val requestTimeSeconds = TimeUnit.NANOSECONDS.toMillis(
            System.nanoTime() - startNanoSecond
        ) / 1000.0

        val statusCode = response?.code ?: 404

        val responseBodyLengthPair = responseBody(response)
        val responseBody = responseBodyLengthPair?.first
        val responseSizeBytes = (responseBodyLengthPair?.second ?: 0L)
            .coerceAtLeast(0)

        val errorMessage = when {
            requestError != null -> requestError.message
            statusCode in 300..599 -> parseErrorMessage(responseBody)
            else -> null
        }

        val status = when {
            requestError != null -> Status.Failed
            requestTimeSeconds >= timeoutInSeconds
                    && responseBody.isNullOrBlank() -> Status.Timeout
            statusCode in 200..299 -> Status.Success
            statusCode in 300..599 -> Status.Error
            else -> Status.NoInternet
        }

        val event = ApiRequestEvent(
            url = urlString,
            statusCode = statusCode,
            status = status,
            errorMessage = errorMessage ?: "",
            responseTimeInSeconds = requestTimeSeconds,
            responseSizeInKB = responseSizeBytes / 1_000.0,
            responseSizeInMB = responseSizeBytes / 1_000_000.0,
        )

        recordApiRequestEvent(event)

        if (response == null) {
            throw (requestError ?: Exception("Request failed with no response"))
        }

        return response
    }

    private fun responseBody(response: Response?): Pair<String?, Long?>? {
        val responseBody = response?.body ?: return null
        val contentLength = responseBody.contentLength()

        if (contentLength == 0L) {
            return null
        }

        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer
        val headers = response.headers

        if ("gzip".equals(headers.get("Content-Encoding"), ignoreCase = true)) {
            var gzippedResponseBody: GzipSource? = null
            try {
                gzippedResponseBody = GzipSource(buffer.clone())
                buffer = okio.Buffer()
                buffer.writeAll(gzippedResponseBody)
            } finally {
                gzippedResponseBody?.close()
            }
        }

        val charset: Charset = responseBody.contentType()?.charset(UTF8) ?: UTF8
        return buffer.clone().readString(charset) to buffer.size
    }

    private fun parseErrorMessage(errorBody: String?): String? {
        return try {
            val messageObject = Gson().fromJson(errorBody, JsonObject::class.java)
            (messageObject.get("developer_message") ?: messageObject.get("message")).asString
        } catch (ex: Exception) {
            errorBody
        }
    }

    private companion object {
        val UTF8: Charset = Charset.forName("UTF-8")
    }
}



