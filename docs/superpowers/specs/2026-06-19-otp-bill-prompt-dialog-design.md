# OTP "Bill Prompt Sent" Dialog — Design

**Date:** 2026-06-19
**Branch:** keyboard-fix
**Status:** Approved (design)

## Problem

After a successful pre-payment OTP, the SDK now calls `payOrder` and navigates
straight to the payment status screen (the post-OTP navigation fix). That
transition feels abrupt ("snappy"). iOS shows a "Success — A bill prompt has been
sent to <number>. Please authorise the payment." dialog with an OK button before
the user proceeds. Android should match for consistency.

## Goal

In the post-OTP MoMo flow, after `payOrder` confirms the prompt was sent, show a
Success dialog telling the user a bill prompt was sent to their number. On OK,
navigate to the payment status screen (which polls). On payment failure, show the
existing error dialog and do not navigate.

## Reuse (no new strings or components)

- Component: `CheckoutMessageDialog` (title, message, positive button,
  `onPositiveClick`).
- Strings (already present in `strings.xml`):
  - `checkout_success` = "Success"
  - `checkout_momo_bill_prompt_msg` = "A bill prompt has been sent to %1$s. Please authorize payment."
  - `checkout_okay` = "Okay"
- This is the same dialog the non-OTP MoMo flow already shows
  (PayOrderScreen.kt:591, 626).

## Behavior change (in `PayOrderScreen`)

The resume effect added for the post-OTP fix
(`LaunchedEffect(viewModel.resumeAfterOtp)`) currently does:

1. `viewModel.payOrder(config, walletType)`
2. immediately `navigator.push(PaymentStatusScreen(...))`

Change to:

1. On OTP success (resume): call `viewModel.payOrder(config, walletType)` and set
   an `awaitingPaymentPrompt` flag. Do NOT navigate yet.
2. When `checkoutUiState.success` and `awaitingPaymentPrompt` is set: clear the
   flag and show the bill-prompt Success dialog with the number from
   `paymentInfo?.accountNumber`.
3. Dialog `onPositiveClick` → `navigator.push(PaymentStatusScreen(providerName =
   paymentInfo?.providerName, config = config, checkoutType =
   checkoutFeesUiState.data?.getCheckoutType))`.

## State

- `awaitingPaymentPrompt: Boolean` — set when the resume fires `payOrder`; gates
  the success dialog so only this path triggers it.
- `showBillPromptDialog: Boolean` — drives rendering of `CheckoutMessageDialog`.

Both are screen-local composable state. The home screen is live and stable while
the dialog is shown (no navigation until OK), so plain `remember` is sufficient;
`rememberSaveable` is acceptable for resilience to configuration changes.

## Error handling

No new code. A failed `payOrder` sets `checkoutUiState.hasError`, which the
existing dialog at PayOrderScreen.kt:501 already renders. In that case
`checkoutUiState.success` is false, so the bill-prompt dialog does not show and
the app does not navigate.

## Wording note

The existing Android string says "Please authorize payment." iOS says "Please
authorise the payment." This design reuses the existing string to keep Android
internally consistent (same dialog as the non-OTP flow). If exact iOS parity is
preferred, update `checkout_momo_bill_prompt_msg` instead — but that also changes
the non-OTP flow's dialog (which is fine, just noted).

## Scope boundaries

- Only the post-OTP MoMo resume path changes.
- No ViewModel, API, or model changes.
- No change to the non-OTP flows.

## Testing

Compile gate plus on-device check: run the MoMo OTP flow; after entering the OTP
and the prompt is sent, the Success dialog appears with the correct number; tap
OK and confirm it navigates to the payment status screen and polls.
