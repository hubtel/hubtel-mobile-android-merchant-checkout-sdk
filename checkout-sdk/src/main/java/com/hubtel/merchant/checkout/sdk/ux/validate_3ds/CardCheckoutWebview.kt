package com.hubtel.merchant.checkout.sdk.ux.validate_3ds

import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.androidx.AndroidScreen
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.VerificationAttempt

internal data class CardCheckoutWebview(
    private val config: CheckoutConfig,
    private val html: String,
    val onFinish: () -> Unit,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                CookieManager.getInstance().setAcceptCookie(true)

                addJavascriptInterface(
                    EnrollmentJsInterface { data ->
                        if (data == CheckoutJsInterface.ENROLLMENT) {
                            onFinish.invoke()
                        }
                    },
                    EnrollmentJsInterface.JS_NAME,
                )
            }
        }, update = {
            val jsInjectedHtml = html.replace(
                "CONTROL_RETURN_IDENTIFIER",
                "${EnrollmentJsInterface.JS_NAME}.postMessage('${EnrollmentJsInterface.FINISH}')"
            )
            // getHtml from 3ds result and replace the parts
            it.loadDataWithBaseURL(
                "https://localhost/", jsInjectedHtml,
                "text/html", "UTF-8", null,
            )
        })
    }
}


internal class EnrollmentJsInterface(
    private val onFinish: (result: Any?) -> Unit
) {

    @JavascriptInterface
    fun postMessage(data: String) {
        onFinish(data)
    }

    companion object {
        const val JS_NAME = "EnrollmentJavascriptInterface"
        const val FINISH = "Finish"
    }
}