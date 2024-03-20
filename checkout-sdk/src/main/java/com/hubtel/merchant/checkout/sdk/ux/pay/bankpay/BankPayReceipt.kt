package com.hubtel.merchant.checkout.sdk.ux.pay.bankpay

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import com.hubtel.merchant.checkout.sdk.R
import com.hubtel.merchant.checkout.sdk.platform.data.source.api.model.response.CheckoutInfo
import com.hubtel.merchant.checkout.sdk.ux.CheckoutActivity
import com.hubtel.merchant.checkout.sdk.ux.components.HBTopAppBar
import com.hubtel.merchant.checkout.sdk.ux.layouts.HBScaffold
import com.hubtel.merchant.checkout.sdk.ux.model.CheckoutConfig
import com.hubtel.merchant.checkout.sdk.ux.pay.order.BusinessResponseInfo
import com.hubtel.merchant.checkout.sdk.ux.theme.Dimens
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme
import com.hubtel.merchant.checkout.sdk.ux.theme.TealPrimary
import com.hubtel.merchant.checkout.sdk.ux.utils.LocalActivity
import com.hubtel.merchant.checkout.sdk.ux.utils.formatMoney
import com.hubtel.merchant.checkout.sdk.ux.utils.removeSpaces
import com.hubtel.merchant.checkout.sdk.ux.utils.toReceiptDateTime
import java.util.Date

internal data class BankPayReceipt(
    val checkoutConfig: CheckoutConfig,
    val checkoutInfo: CheckoutInfo?,
    val businessInfo: BusinessResponseInfo?
) : Screen {

    @Composable
    override fun Content() {
        ScreenContent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    private fun ScreenContent() {
        val context = LocalContext.current
        val activity = LocalActivity.current
        val navigator = LocalNavigator.current

        var isDownloadComplete by remember { mutableStateOf(false) }

        val checkoutActivity = remember(activity) { activity as? CheckoutActivity }

        if (isDownloadComplete)
            PaidSuccessContent(navigator = navigator, checkoutActivity)
        else InvoiceContent(navigator = navigator, context = context) {
            isDownloadComplete = true
        }

    }

    @Composable
    private fun InvoiceContent(
        navigator: Navigator?,
        context: Context,
        downloadComplete: () -> Unit = {}
    ) {
        var isPrinting by remember { mutableStateOf(false) }

        val pageHtml = buildDisplayableReceipt()

        val webView = remember {
            createWebView(context)
        }

        HBScaffold(
            topBar = {
                HBTopAppBar(title = {
                    Text(text = stringResource(id = R.string.checkout_processing_payment))
                }, actions = {
                    TextButton(onClick = {
                        navigator?.pop()
//                        recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCancel)
                    }) {
                        Text(
                            text = stringResource(id = R.string.checkout_cancel),
                            color = HubtelTheme.colors.error,
                        )
                    }
                })
            },
            bottomBar = {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        thickness = 2.dp,
                    )
                    TextButton(
                        onClick = {
                            isPrinting = true
                            toPDF(webView = webView, context) {
                                downloadComplete()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(Dimens.paddingDefault),
                    ) {
                        Text(
                            text = stringResource(id = R.string.checkout_download_pdf),
                            modifier = Modifier.padding(Dimens.paddingSmall)
                        )
                    }
                }
            },
        ) { paddingValues ->
            Card(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(Dimens.paddingDefault),
                elevation = 5.dp
            ) {
                AndroidView(factory = { webView }, update = {
                    it.loadDataWithBaseURL(
                        "https://localhost/",
                        pageHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }, modifier = Modifier.fillMaxSize())
            }
        }
    }

    @Composable
    private fun PaidSuccessContent(navigator: Navigator?, checkoutActivity: Activity?) {
        HBScaffold(
            backgroundColor = HubtelTheme.colors.uiBackground2,
            topBar = {
                HBTopAppBar(title = {
                    Text(text = stringResource(id = R.string.checkout_confirm_order))
                }, actions = {
                    TextButton(onClick = {
                        checkoutActivity?.finish()
//                        recordCheckoutEvent(CheckoutEvent.CheckoutCheckStatusTapCancel)
                    }) {
                        Text(
                            text = stringResource(id = R.string.checkout_cancel),
                            color = HubtelTheme.colors.error,
                        )
                    }
                })
            },
            bottomBar = {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    Divider(
                        color = HubtelTheme.colors.outline,
                        thickness = 2.dp,
                    )
                    TextButton(
                        onClick = {
                            checkoutActivity?.finish()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(Dimens.paddingDefault),
                    ) {
                        Text(
                            text = stringResource(id = R.string.checkout_okay).uppercase(),
                            modifier = Modifier.padding(Dimens.paddingSmall)
                        )
                    }
                }
            },
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .padding(Dimens.paddingDefault),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.width(300.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.checkout_ic_success_teal),
                        contentDescription = null,
                        modifier = Modifier.size(86.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.checkout_download_complete),
                        style = HubtelTheme.typography.h3.copy(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )

                    Text(
                        buildAnnotatedString {
                            append(stringResource(id = R.string.checkout_bank_pay_via))

                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TealPrimary))
                            append(" Ghana.GOV ")
                            pop()

                            append(stringResource(id = R.string.checkout_payment_platfrom))
                        },
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )

                    Text(
                        buildAnnotatedString {
                            append(stringResource(id = R.string.checkout_bank_pay_complete_stage))

                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append(" *718*108#.")
                        },
                        style = HubtelTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Dimens.spacingDefault)
                    )
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun createWebView(context: Context): WebView {
        val w = WebView(context).apply {
            this.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            CookieManager.getInstance().setAcceptCookie(true)
        }
        return w
    }

    private fun toPDF(webView: WebView?, ctx: Context, toggle: () -> Unit = {}) {
        webView?.let {
            val printManager = ctx.getSystemService(Context.PRINT_SERVICE) as PrintManager
            val jobName =
                "${businessInfo?.businessName?.removeSpaces()}_${checkoutInfo?.customerName?.removeSpaces()}-bank-pay-receipt-" + System.currentTimeMillis()

            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            val printAttributes =
                PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build()

            printManager.print(jobName, printAdapter, printAttributes).also {
                toggle()
            }

        }
    }

    private fun buildDisplayableReceipt(): String {
        return """
                <!DOCTYPE html>
    <html lang="en">
      <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Processing Payment</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Nunito+Sans:opsz,wght@6..12,300;6..12,400;6..12,500;6..12,600;6..12,700;6..12,800;6..12,900&display=swap" rel="stylesheet">

        <style>
              body {
                min-width: 330px;
                margin: 0;
                font-family: 'Nunito Sans', sans-serif;
              }
              h1,
              h2,
              h3,
              h4,
              h5,
              h6,
              p {
                margin: 0;
              }
              header {
                text-align: center;
                border-bottom: 1px solid #eee;
                position: relative;
                box-shadow: 2px 2px #eee;
                padding: 40px 20px 10px 20px;
              }
        
              header h2 {
                font-weight: 400;
              }
              header p {
                font-weight: 700;
              }
              section {
                padding: 30px 13px 20px;
                border: 1px solid #eee;
                margin: auto;
                box-shadow: 2px 4px #eee;
                border-radius: 8px;
                display: flex;
                
                justify-content: center;
                max-width: 500px;
                flex-direction: column;
              }
        
              .border-thin {
                border-bottom: 1px solid #eee;
              }
              .border-thick {
                border-bottom: 2px solid #eee;
              }
        
              .bottom-padding p,
              h1,
              h2,
              h3,
              h4,
              h5,
              h6 {
                padding-bottom: 10px;
              }
              button {
                padding: 20px;
                text-transform: uppercase;
                background-color: #01c7b1;
                color: #ffffff;
                border-radius: 8px;
                border: none;
                width: 100%;
                font-size: 18px;
                font-weight: 400;
              }
              .cancel {
                color: #ff0000;
                position: absolute;
                top: 45px;
                right: 16px;
              }
        
              .center {
                text-align: center;
              }
        
              .fixed-bottom {
                position: fixed;
                bottom: 0;
              }
        
              .flex-between {
                display: flex;
                justify-content: space-between;
                margin-bottom: 30px;
              }
              .flex-between:last-child {
                margin-bottom: 0;
              }
        
              .no-space{
                white-space: nowrap;
              }
        
              ol {
                padding-inline-start: 13px;
              }
        
              ol li {
                padding-bottom: 10px;
              }
        
              ol li:last-child {
                padding-bottom: 0;
              }
              .place-end {
                text-align: end;
              }
        
              .place-end span {
                background-color: #ffd7d5;
                color: #b22922;
                border-radius: 15px;
                padding: 5px 10px;
                font-weight: 700;
              }
        
              .page-margin-x {
                margin: 0 14px;
              }
              .steps {
                margin-top: 30px;
              }
              .smaller {
                font-size: 10px;
              }
              .small {
                font-size: 14px;
              }
        
              table {
                width: 100%;
                margin-top: 30px;
                border-bottom: 2px solid #e6e6e6;
              }
              tfoot tr:first-child {
                border-top: 1px solid #eee;
              }
        
              /* td{
                white-space: nowrap;
              } */
              tr {
                display: flex;
                justify-content: space-between;
                padding: 10px;
              }
              thead {
                background-color: #e6eaed;
              }
              .text-muted {
                color: #9cabb8;
              }
            </style>
  </head>
  <body class="">
    <section>
      <div class="">
        <div class="flex-between">
          <div>
            <img
              src="${businessInfo?.businessLogoURL}"
              alt="logo"
              height="92px"
              width="92px"
            />
          </div>
          <div class="place-end bottom-padding">
            <h4>Cash/Cheque Pay-In-Slip</h4>
            <p class="small">${checkoutInfo?.invoiceNumber}</p>
            <span class="smaller">Unpaid</span>
          </div>
        </div>

        <div class="flex-between">
          <div class="bottom-padding">
            <h5 class="">${businessInfo?.businessName}</h5>
            <p class="small"></p>
            <p class="small"></p>
          </div>
          <div class="place-end bottom-padding">
            <p class="text-muted small">To:</p>
            <h6 class="small">${checkoutInfo?.customerName ?: ""}</h6>
            <p class="small"><a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="650f110d0a0815160a0b250208040c094b060a08">${checkoutInfo?.email ?: ""}</a></p>
            <p class="small">${checkoutInfo?.customerMsisdn ?: ""}</p>
          </div>
        </div>
        <div class="flex-between">
          <div class="bottom-padding">
            <p class="small text-muted">Payment Method</p>
            <h6 class="small">Bankpay at Any bank</h6>
          </div>
          <div class="place-end bottom-padding">
            <p class="text-muted small">Date • Time</p>

            <p class="small">${Date().toReceiptDateTime()}</p>
          </div>
        </div>
      </div>
      <div>
        <table class="small">
          <thead>
            <tr>
              <th><p>Description</p></th>
              <th><p class="no-space">Amount</p></th>
            </tr>
          </thead>
          <tr>
            <td><p>${checkoutInfo?.description}</p></td>
            <td><p class="no-space">${
            checkoutInfo?.amountAfterCharges?.formatMoney(includeDecimals = true)
        }</p></td>
          </tr>

          <tfoot>
            <tr>
              <td><p>Fees</p></td>
              <td><p class="no-space">${checkoutInfo?.charges?.formatMoney(includeDecimals = true)}</p></td>
            </tr>       
            <tr>
              <td><h4>Amount to pay</h4></td>
              <td><h4 class="no-space">${checkoutInfo?.amount?.formatMoney(includeDecimals = true)}</h4></td>
            </tr>
          </tfoot>
        </table>
      </div>
      <div class="steps">
        <h5>
          To complete your transaction, please pay for this invoice with the
          following steps
        </h5>
        <ol>
          <li class="small">
            <p>
              Pay at your bank branch via Ghana.GOV Payment Platform or send
              this invoice to your relationship manager at your bank
            </p>
          </li>
          <li class="small">
            <p>
              Mention the Ghana.GOV Pay-In-Slip Number : ${checkoutInfo?.invoiceNumber} to pay
              at the teller of your bank
            </p>
          </li>
          <li class="small">
            <p>
              Once complete, you may check the status of this Pay-In-Slip in
              your payment history
            </p>
          </li>
        </ol>
      </div>
    </section>
  <script data-cfasync="false" src="/cdn-cgi/scripts/5c5dd728/cloudflare-static/email-decode.min.js"></script><script defer src="https://static.cloudflareinsights.com/beacon.min.js/v8b253dfea2ab4077af8c6f58422dfbfd1689876627854" integrity="sha512-bjgnUKX4azu3dLTVtie9u6TKqgx29RBwfj3QXYt5EKfWM/9hPSAI/4qcV5NACjwAo8UtTeWefx6Zq5PHcMm7Tg==" data-cf-beacon='{"rayId":"814d00f44f24033a","token":"5f4f22e669d54423a93b2e43037c9d74","version":"2023.8.0","si":100}' crossorigin="anonymous"></script>
</body>
</html>
        """.trimIndent()
    }
}
