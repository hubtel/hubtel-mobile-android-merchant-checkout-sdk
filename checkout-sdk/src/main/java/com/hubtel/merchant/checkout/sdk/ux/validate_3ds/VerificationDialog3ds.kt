package com.hubtel.merchant.checkout.sdk.ux.validate_3ds

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import com.hubtel.core_ui.components.custom.HBTopAppBar
import com.hubtel.core_ui.layouts.HBScaffold
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.ux.pay.order.CheckoutStep
import com.hubtel.merchant.checkout.sdk.ux.pay.order.ThreeDSSetupState
import com.hubtel.merchant.checkout.sdk.ux.pay.order.Verification3dsState
import timber.log.Timber

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun VerificationDialog3ds(
    step: CheckoutStep,
    setupState: ThreeDSSetupState,
    verificationState: Verification3dsState,
    onCollectionComplete: () -> Unit,
    onCardVerified: () -> Unit,
    onBackClick: () -> Unit,
) {
    var shouldShowVerificationPrompt by remember { mutableStateOf(false) }

    val pageHtml = remember(shouldShowVerificationPrompt) {
        if (!shouldShowVerificationPrompt) {
            buildDeviceDataCollectionIframe(setupState)
        } else {
            build3dsVerificationHtml(verificationState)
        }
    }

    HBScaffold(
        topBar = {
            Column {
                HBTopAppBar(
                    onNavigateUp = onBackClick,
                    title = {}
                )

                Text(
                    text = buildAnnotatedString {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        append(stringResource(R.string.checkout_additional_verification_title))
                        pop()
                        append("\n")
                        append(stringResource(R.string.checkout_additional_verification_subtitle))
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(Dimens.paddingDefault)
                )
            }
        },
        backgroundColor = HubtelTheme.colors.uiBackground2,
        modifier = Modifier.alpha(if (shouldShowVerificationPrompt) 1f else 0f)
    ) {
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
                        VerificationDialogJavascriptInterface(
                            onCollectionComplete = { onCollectionComplete() },
                            onVerificationComplete = onCardVerified
                        ),
                        VerificationDialogJavascriptInterface.JS_NAME,
                    )

                    webViewClient = WebViewClient()
                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Timber.tag(TAG).i(
                                """level: ${consoleMessage?.messageLevel()}\n 
                                message: ${consoleMessage?.message()} \n
                                --- line: ${consoleMessage?.lineNumber()}"""
                            )

                            return super.onConsoleMessage(consoleMessage)
                        }
                    }
                }
            },
            update = {
                it.loadDataWithBaseURL(
                    "https://localhost/", pageHtml,
                    "text/html", "UTF-8", null,
                )
            },
            modifier = Modifier.fillMaxSize()
        )
    }


    LaunchedEffect(step) {
        if (step == CheckoutStep.VERIFY_CARD) {
            shouldShowVerificationPrompt = true
//            recordCheckoutEvent(CheckoutEvent.CheckoutDsBrowserViewPageDsBrowser)
        }
    }
}

private const val TAG = "VerificationDialog3ds"

internal fun buildDeviceDataCollectionIframe(state: ThreeDSSetupState): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta http-equiv="X-UA-Compatible" content="IE=edge">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title></title>
            </head>
            <body>
            <iframe id="cardinal_collection_iframe" 
                name="collectionIframe" height="10" width="10" 
                style="display: none;">
            </iframe>
            <form id="cardinal_collection_form" 
                method="POST" target="collectionIframe" 
                action="https://centinelapi.cardinalcommerce.com/V1/Cruise/Collect">
                <input id="cardinal_collection_form_input" type="hidden" name="JWT" 
                value="${state.accessToken}">
            </form>    
            </body>
            <script>
                window.onload = function() {
                    var cardinalCollectionForm = document.querySelector('#cardinal_collection_form'); 
                    if(cardinalCollectionForm) {
                        cardinalCollectionForm.submit();
                    }
                }
                
                window.addEventListener("message", function(event) {
                    if (event.origin === "https://centinelapi.cardinalcommerce.com") {
                        //console.log(event.data); 
                        window.${VerificationDialogJavascriptInterface.JS_NAME}.onDataCollectionCompleted()
                    }
                }, false);
            </script>
        </html>
    """.trimIndent()
}

private fun build3dsVerificationHtml(state: Verification3dsState): String {
    return """
      <!DOCTYPE html>
      <html lang="en">
      <head>
        <meta charset="UTF-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title></title>
        <style>
          iframe{      
            display: block;
            border: none;
            height: 100vh;
            width: 100vw;
          }
        </style>
      </head>
      <body>
        <div>
          <iframe name="step-up-iframe"></iframe>
          <form
            id="step_up_form"
            target="step-up-iframe"
            method="POST"
            action="https://centinelapi.cardinalcommerce.com/V2/Cruise/StepUp"
          >
            <input
              name="JWT"
              class="form-control"
              type="hidden"
              value="${state.jwt}"
            />
            <input
              id="md-input"
              type="hidden"
              name="MD"
            />
            <button class="btn btn-success" style="display: none;">Step up</button>
          </form>
        </div>
        <script type="text/javascript">
          window.onload = function() {
            let mdInput = document.querySelector("#md-input");
            mdInput.value = `${state.customData}`;
    
            window.addEventListener("message", function handler(event) {
              if (event.origin === "https://cybersourcecallbacks.hubtel.com") {
                window.${VerificationDialogJavascriptInterface.JS_NAME}.onCardVerificationCompleted()
                this.removeEventListener("message", handler);
              }
            });
    
            let stepUpForm = document.querySelector("#step_up_form");
            if (stepUpForm) {
              stepUpForm.submit();
            }
          }
        </script>
      </body>
    </html>
        """.trimIndent()
}

private class VerificationDialogJavascriptInterface(
    private val onCollectionComplete: () -> Unit,
    private val onVerificationComplete: () -> Unit
) {

    @JavascriptInterface
    fun onDataCollectionCompleted() {
        Timber.tag(JS_NAME).i("Device Info Collection Complete")
        onCollectionComplete()
    }

    @JavascriptInterface
    fun onCardVerificationCompleted() {
        Timber.tag(JS_NAME).i("3DS Verifica†ion Success")
        onVerificationComplete()
    }

    companion object {
        const val JS_NAME = "VerificationDialogJavascriptInterface"
    }
}