package com.hubtel.feature_checkout.ui.validate_3ds

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.hubtel.core_ui.components.custom.HBProgressDialog
import com.hubtel.feature_checkout.R
import com.hubtel.sdk.checkout.ux.pay.order.ThreeDSSetupState
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun Collect3dsDeviceInfoDialog(
    setupState: ThreeDSSetupState,
    onCollectionComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageHtml = remember(setupState) {
        buildDeviceDataCollectionIframe(setupState)
    }

    Box(modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    this.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    CookieManager.getInstance().setAcceptCookie(true)

                    addJavascriptInterface(
                        CollectDeviceInfoJavascriptInterface(onCollectionComplete),
                        CollectDeviceInfoJavascriptInterface.JS_NAME,
                    )

                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                }
            },
            update = {
                it.loadDataWithBaseURL(
                    "https://localhost/", pageHtml,
                    "text/html", "UTF-8", null,
                )
            },
            modifier = Modifier
                .size(0.dp, 0.dp)
        )

        HBProgressDialog(
            message = "${stringResource(R.string.fc_please_wait)}..."
        )
    }
}

internal class CollectDeviceInfoJavascriptInterface(
    private val onComplete: () -> Unit
) {

    @JavascriptInterface
    fun onDataCollectionCompleted() {
        Timber.tag(JS_NAME).i("Device Info Collection Complete")
        onComplete()
    }

    companion object {
        const val JS_NAME = "CollectDeviceInfoJavascriptInterface"
    }
}

//private fun buildDeviceDataCollectionIframe(accessToken: String): String {
//    return """
//        <!DOCTYPE html>
//        <html lang="en">
//            <head>
//                <meta charset="UTF-8">
//                <meta http-equiv="X-UA-Compatible" content="IE=edge">
//                <meta name="viewport" content="width=device-width, initial-scale=1.0">
//                <title></title>
//            </head>
//            <body>
//            <iframe id="cardinal_collection_iframe"
//                name="collectionIframe" height="10" width="10"
//                style="display: none;">
//            </iframe>
//            <form id="cardinal_collection_form"
//                method="POST" target="collectionIframe"
//                action="https://centinelapi.cardinalcommerce.com/V1/Cruise/Collect">
//                <input id="cardinal_collection_form_input" type="hidden" name="JWT"
//                value="$accessToken">
//            </form>
//            </body>
//            <script>
//                window.onload = function() {
//                    var cardinalCollectionForm = document.querySelector('#cardinal_collection_form');
//                    if(cardinalCollectionForm) {
//                        cardinalCollectionForm.submit();
//                    }
//                }
//
//                window.addEventListener("message", function(event) {
//                    if (event.origin === "https://centinelapi.cardinalcommerce.com") {
//                        console.log(event.data);
//                        window.${CollectDeviceInfoJavascriptInterface.JS_NAME}.onDataCollectionCompleted()
//                    }
//                }, false);
//            </script>
//        </html>
//    """.trimIndent()
//}