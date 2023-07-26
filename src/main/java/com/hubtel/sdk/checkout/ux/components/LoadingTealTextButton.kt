package com.hubtel.sdk.checkout.ux.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hubtel.core_ui.components.custom.TealButton
import com.hubtel.core_ui.theme.Dimens
import com.hubtel.core_ui.theme.HubtelTheme

@Composable
internal fun LoadingTealTextButton(
    text: String,
    onClick:  () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    loading: Boolean = false
) {
    TealButton(
        onClick = { if (!loading) onClick() },
        enabled = enabled,
        modifier = modifier
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = HubtelTheme.colors.colorOnPrimary,
                modifier = Modifier.size(30.dp)
            )
        } else {
            Text(
                text = text.uppercase(),
                style = HubtelTheme.typography.button,
                modifier = Modifier.padding(Dimens.paddingNano)
            )
        }
    }
}