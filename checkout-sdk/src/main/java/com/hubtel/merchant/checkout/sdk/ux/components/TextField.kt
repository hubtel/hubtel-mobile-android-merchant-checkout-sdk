package com.hubtel.merchant.checkout.sdk.ux.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import com.hubtel.merchant.checkout.sdk.ux.theme.HubtelTheme

@Composable
fun HBTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = HubtelTheme.typography.body1,
    isError: Boolean = false,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = HubtelTheme.colors.inputBackground,
    label: (@Composable () -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = textStyle,
        isError = isError,
        label = label,
        maxLines = maxLines,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        shape = HubtelTheme.shapes.small,
        readOnly = readOnly,
        enabled = enabled,
        colors = TextFieldDefaults.textFieldColors(
            textColor = HubtelTheme.colors.textPrimary,
            backgroundColor = backgroundColor,
            cursorColor = HubtelTheme.colors.colorPrimary,
            placeholderColor = HubtelTheme.colors.textHint,
            leadingIconColor = HubtelTheme.colors.textHint,
            trailingIconColor = HubtelTheme.colors.textHint,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}


@Composable
fun HBTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = HubtelTheme.typography.body1,
    isError: Boolean = false,
    placeholder: (@Composable () -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = HubtelTheme.colors.inputBackground,
    label: (@Composable () -> Unit)? = null,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = textStyle,
        isError = isError,
        label = label,
        maxLines = maxLines,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        shape = HubtelTheme.shapes.small,
        readOnly = readOnly,
        enabled = enabled,
        colors = TextFieldDefaults.textFieldColors(
            textColor = HubtelTheme.colors.textPrimary,
            backgroundColor = backgroundColor,
            cursorColor = HubtelTheme.colors.colorPrimary,
            placeholderColor = HubtelTheme.colors.textHint,
            leadingIconColor = HubtelTheme.colors.textHint,
            trailingIconColor = HubtelTheme.colors.textHint,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}
