package com.hubtel.merchant.checkout.sdk.ux.validate_3ds

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

internal data class CardCheckoutWebview(
    private val config: CheckoutConfig,
    private val html: String,
    val onFinish: () -> Unit,
) : Screen {

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        HBScaffold(
            topBar = {
                Column {
                    HBTopAppBar(
                        onNavigateUp = { navigator?.pop() },
                        title = {},
                    )
                }
            },
            backgroundColor = HubtelTheme.colors.uiBackground2,
        ) {
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
                            if (data == EnrollmentJsInterface.FINISH) {
                                onFinish.invoke()
                            }
                        },
                        EnrollmentJsInterface.JS_NAME,
                    )

                    val jsInjectedHtml = html.replace(
                        "CONTROL_RETURN_IDENTIFIER",
                        "${EnrollmentJsInterface.JS_NAME}.postMessage('${EnrollmentJsInterface.FINISH}')"
                    )
                    // getHtml from 3ds result and replace the parts
                    loadDataWithBaseURL(
                        "https://localhost/", jsInjectedHtml,
                        "text/html", "UTF-8", null,
                    )
                }
            })
        }
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