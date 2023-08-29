package com.hubtel.merchant.checkout.sdk.ux.model

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.hubtel.merchant.checkout.sdk.ux.components.pluralResource

@Deprecated("Use UiState 2 instead")
data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val success: Boolean = false,
    val error: String? = null
) {
    val hasError: Boolean
        get() = error != null
}

data class UiState2<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val success: Boolean = false,
    val error: UiText? = null
) {
    val hasError: Boolean
        get() = error != null

    val hasData: Boolean
        get() = data != null
}

sealed class UiText {
    data class DynamicString(val value: String): UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ): UiText()

    class PluralResource(
        @PluralsRes val resId: Int,
        val quantity: Int,
        vararg val args: Any,
    ): UiText()

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> this.value
            is StringResource -> stringResource(this.resId, *this.args)
            is PluralResource -> pluralResource(this.resId, this.quantity, *this.args)
        }
    }
}