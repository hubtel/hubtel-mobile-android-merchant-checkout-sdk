package com.hubtel.merchant.checkout.sdk.ux.components

import android.content.res.Resources
import android.os.Build
import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest



@Composable
@ReadOnlyComposable
fun pluralResource(@PluralsRes id: Int, quantity: Int): String {
    return resources().getQuantityString(id, quantity)
}

@Composable
@ReadOnlyComposable
fun pluralResource(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String {
    return resources().getQuantityString(id, quantity, *formatArgs)
}

/**
 * A composable function that returns the [Resources]. It will be recomposed when [Configuration]
 * gets updated.
 */
@Composable
@ReadOnlyComposable
private fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}
