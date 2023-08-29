package com.hubtel.merchant.checkout.sdk.ux.utils

import androidx.compose.runtime.MutableState
import com.hubtel.merchant.checkout.sdk.ux.model.UiState2

fun <T> MutableState<T>.update(block: (T) -> T) {
    val updatedValue = block.invoke(this.value)
    this.value = updatedValue
}


fun <T> MutableState<UiState2<T>>.reset() {
    this.value = UiState2<T>()
}