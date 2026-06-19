# OTP "Bill Prompt Sent" Dialog Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** After a successful post-OTP MoMo payment call, show a "bill prompt sent" Success dialog; navigate to the payment status screen only when the user taps OK.

**Architecture:** Modify the post-OTP resume effect in `PayOrderScreen` so it fires `payOrder` but waits for success and shows the existing `CheckoutMessageDialog` (reusing the `checkout_momo_bill_prompt_msg` string) before navigating. Payment failure is already handled by the existing `checkoutUiState.hasError` dialog.

**Tech Stack:** Kotlin, Jetpack Compose, Voyager.

## Global Constraints

- Reuse existing strings (`checkout_success`, `checkout_momo_bill_prompt_msg`, `checkout_okay`) and `CheckoutMessageDialog`. Do NOT add new strings or components.
- Only the post-OTP MoMo resume path changes. No ViewModel/API/model changes, no change to non-OTP flows.
- The success dialog must only trigger for the OTP resume path (gated by the `awaitingPaymentPrompt` flag), never for other flows whose `payOrder` also sets `checkoutUiState.success`.
- The dialog number comes from `paymentInfo?.accountNumber`.
- No unit test — Compose dialog/navigation has no meaningful unit test here; the gate is a clean compile plus on-device check.

---

### Task 1: Show bill-prompt dialog after OTP, navigate on OK

**Files:**
- Modify: `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/order/PayOrderScreen.kt`

**Interfaces:**
- Consumes (already in scope): `viewModel`, `walletUiState`, `paymentInfo`, `checkoutFeesUiState`, `checkoutUiState`, `navigator`, `config`, `CheckoutMessageDialog`, `PaymentStatusScreen`, `R.string.*`.
- Produces: two new screen-local state flags and a reused dialog; no new public API.

- [ ] **Step 1: Add the two state flags**

Find (PayOrderScreen.kt:161):

```kotlin
        var showCancelDialog by remember { mutableStateOf(false) }
```

Add immediately after it:

```kotlin
        var showCancelDialog by remember { mutableStateOf(false) }

        // Post-OTP bill-prompt dialog state.
        var awaitingPaymentPrompt by remember { mutableStateOf(false) }
        var showBillPromptDialog by remember { mutableStateOf(false) }
```

- [ ] **Step 2: Change the resume effect to wait instead of navigating**

Replace the entire current resume effect:

```kotlin
        LaunchedEffect(viewModel.resumeAfterOtp) {
            if (viewModel.resumeAfterOtp) {
                viewModel.resumeAfterOtp = false
                // OTP verified: call the payment endpoint directly, then go to the
                // status screen to poll. payOrder() resolves fees with the correct
                // (mtn-gh) channel internally and sends the prompt. We bypass the UI
                // step machine because its fee gate uses the saved wallet's
                // direct-debit channel, which 404s ("fees not set") and stalls.
                walletUiState.payOrderWalletType?.let { walletType ->
                    viewModel.payOrder(config, walletType)
                }
                navigator?.push(
                    PaymentStatusScreen(
                        providerName = paymentInfo?.providerName,
                        config = config,
                        checkoutType = checkoutFeesUiState.data?.getCheckoutType,
                    )
                )
            }
        }
```

with:

```kotlin
        LaunchedEffect(viewModel.resumeAfterOtp) {
            if (viewModel.resumeAfterOtp) {
                viewModel.resumeAfterOtp = false
                // OTP verified: fire the payment endpoint. payOrder() resolves fees
                // with the correct (mtn-gh) channel internally and sends the prompt.
                // Wait for success, then show the "bill prompt sent" dialog before
                // navigating to the status screen (instead of jumping straight there).
                walletUiState.payOrderWalletType?.let { walletType ->
                    viewModel.payOrder(config, walletType)
                }
                awaitingPaymentPrompt = true
            }
        }

        LaunchedEffect(checkoutUiState, awaitingPaymentPrompt) {
            if (awaitingPaymentPrompt && checkoutUiState.success) {
                awaitingPaymentPrompt = false
                showBillPromptDialog = true
            }
        }
```

- [ ] **Step 3: Render the dialog**

Immediately after the block added in Step 2 (after the new
`LaunchedEffect(checkoutUiState, awaitingPaymentPrompt)`), add:

```kotlin
        if (showBillPromptDialog) {
            CheckoutMessageDialog(
                onDismissRequest = { },
                titleText = stringResource(R.string.checkout_success),
                message = stringResource(
                    R.string.checkout_momo_bill_prompt_msg,
                    paymentInfo?.accountNumber ?: "",
                ),
                positiveText = stringResource(R.string.checkout_okay),
                onPositiveClick = {
                    showBillPromptDialog = false
                    navigator?.push(
                        PaymentStatusScreen(
                            providerName = paymentInfo?.providerName,
                            config = config,
                            checkoutType = checkoutFeesUiState.data?.getCheckoutType,
                        )
                    )
                },
            )
        }
```

- [ ] **Step 4: Compile the SDK module**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :checkout-sdk:assembleDebug`
Expected: `BUILD SUCCESSFUL`. No unresolved references for `awaitingPaymentPrompt`, `showBillPromptDialog`, `CheckoutMessageDialog`, or the reused string resources.

- [ ] **Step 5: Commit**

```bash
git add checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/order/PayOrderScreen.kt
git commit -m "feat: show bill-prompt success dialog after OTP before status screen"
```

---

### Task 2: Manual device verification

**Files:** none (verification only).

- [ ] **Step 1: Build and install the demo app**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :app:installDebug`
Expected: `BUILD SUCCESSFUL`, app installed.

- [ ] **Step 2: Verify the dialog and navigation**

Run the MoMo OTP flow. After entering the OTP and the prompt is sent, confirm:
- A "Success" dialog appears: "A bill prompt has been sent to <number>. Please authorize payment." with the correct `paymentInfo` number.
- Tapping **OK** navigates to the payment status screen, which polls.
- The dialog does not appear before the payment call succeeds.

- [ ] **Step 3: Verify failure path**

If a `payOrder` failure can be reproduced, confirm the existing error dialog
shows instead of the success dialog, and the app does not navigate to status.

- [ ] **Step 4: Record results**

Note any case where the dialog shows the wrong number, appears at the wrong time,
or OK does not navigate. If all pass, the feature is complete.
