# Post-OTP → Payment Status Navigation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** After a successful post-payment OTP, navigate the user to `PaymentStatusScreen` instead of dead-ending on the home screen.

**Architecture:** The post-payment OTP push (`PAYMENT_COMPLETED` → `skipOtp == false`) currently has an empty `onFinish`. `OtpVerifyScreen` runs `onFinish()` then `navigator.pop()`, so we cannot push from inside `onFinish`. Instead, set a `rememberSaveable` flag in `onFinish`, let the OTP screen pop back home, and a `LaunchedEffect` on the home screen reacts and pushes `PaymentStatusScreen` — the same state-then-navigate pattern the existing pre-payment OTP path uses.

**Tech Stack:** Kotlin, Jetpack Compose, Voyager navigation.

## Global Constraints

- Only the post-payment OTP path (Entry B) changes. Do NOT modify `handleOtpFlow` (Entry A) or any other OTP push.
- The flag MUST be `rememberSaveable` (not `remember`) — Voyager disposes the home screen's composition while the OTP screen is on top, and a plain `remember` value would not survive.
- Use the exact `PaymentStatusScreen` arguments already used by the existing MoMo success path: `providerName = paymentInfo?.providerName`, `config = config`, `checkoutType = checkoutFeesUiState.data?.getCheckoutType`.
- No ViewModel, API, or model changes.
- No unit test — Compose navigation/IME-style flow has no meaningful unit test here; the automated gate is a clean compile, correctness confirmed by on-device E2E (Task 2).

---

### Task 1: Navigate to PaymentStatusScreen after post-payment OTP success

**Files:**
- Modify: `checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/order/PayOrderScreen.kt`

**Interfaces:**
- Consumes (already in scope in `PayOrderScreenContent`): `navigator` (`LocalNavigator.current`), `paymentInfo`, `checkoutFeesUiState`, `config`, and `PaymentStatusScreen` (already imported).
- Produces: no new public API; a new internal `rememberSaveable` flag and a `LaunchedEffect` that performs the navigation.

- [ ] **Step 1: Add the saveable flag**

Find this line (PayOrderScreen.kt:161):

```kotlin
        var showCancelDialog by remember { mutableStateOf(false) }
```

Add immediately after it:

```kotlin
        var showCancelDialog by remember { mutableStateOf(false) }

        var navigateToStatusAfterOtp by rememberSaveable { mutableStateOf(false) }
```

(`rememberSaveable` and `mutableStateOf` are already imported in this file.)

- [ ] **Step 2: Set the flag from the post-payment OTP `onFinish`**

In the `PAYMENT_COMPLETED` branch, inside the `if (checkoutUiState.data?.skipOtp == false)` block, the pushed `OtpVerifyScreen` has an empty `onFinish`. Replace it. Find this exact block (it is the only empty `onFinish` in the file):

```kotlin
                                paymentChannel = paymentInfo?.channel ?: "",
                                onFinish = {

                                },
```

Replace with:

```kotlin
                                paymentChannel = paymentInfo?.channel ?: "",
                                onFinish = {
                                    navigateToStatusAfterOtp = true
                                },
```

- [ ] **Step 3: Add the navigation LaunchedEffect**

Find this block (PayOrderScreen.kt:889):

```kotlin
        LaunchedEffect(Unit) {
            val orderItems = listOf(config.toPurchaseOrderItem())
```

Insert this new `LaunchedEffect` immediately before it:

```kotlin
        LaunchedEffect(navigateToStatusAfterOtp) {
            if (navigateToStatusAfterOtp) {
                navigateToStatusAfterOtp = false
                navigator?.push(
                    PaymentStatusScreen(
                        providerName = paymentInfo?.providerName,
                        config = config,
                        checkoutType = checkoutFeesUiState.data?.getCheckoutType,
                    )
                )
            }
        }

        LaunchedEffect(Unit) {
            val orderItems = listOf(config.toPurchaseOrderItem())
```

- [ ] **Step 4: Compile the SDK module**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :checkout-sdk:assembleDebug`
Expected: `BUILD SUCCESSFUL`. No unresolved references for `navigateToStatusAfterOtp`, `PaymentStatusScreen`, `paymentInfo`, `checkoutFeesUiState`, or `navigator`.

- [ ] **Step 5: Commit**

```bash
git add checkout-sdk/src/main/java/com/hubtel/merchant/checkout/sdk/ux/pay/order/PayOrderScreen.kt
git commit -m "fix: navigate to payment status screen after post-payment OTP"
```

---

### Task 2: Manual device verification

**Files:** none (verification only).

- [ ] **Step 1: Build and install the demo app**

Run: `JAVA_HOME=/home/soulpee/.jdks/jbr-17.0.14 ./gradlew :app:installDebug`
Expected: `BUILD SUCCESSFUL`, app installed.

- [ ] **Step 2: Verify the post-OTP navigation**

Complete a MoMo payment for a merchant whose checkout response returns
`skipOtp == false` (the path that shows the OTP screen after payment). Enter a
valid OTP. Confirm:
- On OTP success, the app navigates to `PaymentStatusScreen` (no longer stuck on
  the home screen).
- The status screen shows/polls the transaction status.
- Pressing back from the status screen returns to the checkout home screen.

- [ ] **Step 3: Verify no regression on other paths**

- A MoMo payment where `skipOtp == true` (no OTP) still reaches its status/success
  flow as before.
- Bank card and bank pay flows are unaffected (they use their own
  `PAYMENT_COMPLETED` branches).

- [ ] **Step 4: Record results**

Note any path where navigation is missing or doubled. If all pass, the fix is
complete.
