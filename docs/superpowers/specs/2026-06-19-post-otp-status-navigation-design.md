# Post-OTP → Payment Status Navigation — Design

**Date:** 2026-06-19
**Branch:** keyboard-fix
**Status:** Approved (design)

## Problem

After a successful OTP verification on the active (post-payment) checkout path,
the flow dead-ends on the home screen instead of proceeding to the payment
status screen. PM feedback: after OTP success the flow must reach the
check-status screen.

## Investigation summary

`PayOrderScreen` has two OTP entry points:

- **Entry A — `handleOtpFlow`** (pre-payment OTP): gated on
  `businessInfoUiState.data?.requireMobileMoneyOtp == true`. This flag is
  `false` for the current merchant, so the path is **dormant**. It is also
  broken as written (pushes the OTP screen with empty
  `clientReference`/`preApprovalId` because checkout has not run yet, and does
  not check the `sendOtpToUser` result). **Out of scope** — see Follow-up.

- **Entry B — post-payment OTP** (the active, working path): the payment
  endpoint (`payOrder`) runs during the `CHECKOUT` step. The checkout response
  carries `CheckoutInfo.skipOtp`. At `PAYMENT_COMPLETED`, when
  `checkoutUiState.data?.skipOtp == false`, the app pushes `OtpVerifyScreen`
  (PayOrderScreen.kt:841-858) with **`onFinish = { }` (empty)**. On OTP success
  the screen calls `onFinish()` then `navigator.pop()` back to home, and nothing
  navigates to the status screen. This empty `onFinish` is the bug.

## Decision

Payment is already submitted before the OTP on Entry B, so on OTP success the
app navigates directly to `PaymentStatusScreen` (which polls/checks transaction
status). No additional payment call is made after OTP.

## Constraint that shapes the implementation

`OtpVerifyScreen` runs `onFinish()` **then** `navigator.pop()`. Pushing
`PaymentStatusScreen` directly inside `onFinish` would be undone by the
subsequent `pop()`. The codebase already solves this for Entry A: `onFinish`
sets a `rememberSaveable` state, the OTP screen pops, and a `LaunchedEffect` on
the home screen reacts after it becomes visible again. We follow the same
pattern. A plain `remember` boolean is insufficient because Voyager disposes the
home screen's composition while the OTP screen is on top; the flag must be
`rememberSaveable` to survive that disposal.

## Changes (all in `PayOrderScreen.kt`)

1. **Add a saveable flag** alongside the other screen state (near the other
   `remember`/`rememberSaveable` declarations, ~line 161-208):

   ```kotlin
   var navigateToStatusAfterOtp by rememberSaveable { mutableStateOf(false) }
   ```

2. **Set the flag from the post-payment OTP `onFinish`** (PayOrderScreen.kt:853),
   replacing the empty lambda:

   ```kotlin
   onFinish = {
       navigateToStatusAfterOtp = true
   },
   ```

3. **Add a `LaunchedEffect`** (near the other `LaunchedEffect` blocks in
   `PayOrderScreenContent`) that navigates once the flag is set, then resets it:

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
   ```

   These are the same `PaymentStatusScreen` arguments the existing MoMo success
   path already uses (e.g. PayOrderScreen.kt:566, 865). After the OTP screen pops
   back to home, this fires and navigates. Resulting back stack:
   `[PayOrderScreen, PaymentStatusScreen]`.

## Scope boundaries

- Only the post-payment OTP path (Entry B) is changed.
- No ViewModel, API, or model changes.
- Entry A (`handleOtpFlow`) is left untouched.

## Follow-up (not in this change)

Entry A is dormant and broken: if a merchant enables `requireMobileMoneyOtp`,
its OTP would verify against an empty `clientReference`/`preApprovalId` and
likely fail, and it pushes the OTP screen without checking the `sendOtpToUser`
result. Flag to backend/PM; address separately if that flag will be enabled.

## Risks / edge cases

- **Double navigation:** guarded by resetting `navigateToStatusAfterOtp = false`
  inside the effect before pushing, so it fires once per OTP success.
- **OTP failure:** unchanged — `onFinish` only runs on success (the screen shows
  its error dialog otherwise), so the flag is never set on failure.
- **Other wallet types (bank card, bank pay):** unaffected — they have their own
  `PAYMENT_COMPLETED` navigation branches and do not use this OTP path.

## Testing

Consistent with this branch: compile gate plus on-device E2E — complete a MoMo
payment for a merchant whose checkout response returns `skipOtp == false`, enter
the OTP, and confirm the app navigates to `PaymentStatusScreen`.
