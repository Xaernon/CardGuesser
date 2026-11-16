package com.example.cardguesser.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

/**
 * An image fetched from the internet. [onFailed] fires when it cannot be loaded (e.g. offline);
 * [onAspectRatio] reports width / height once it has loaded, so callers can frame it without cropping.
 */
@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onFailed: () -> Unit = {},
    onAspectRatio: (Float) -> Unit = {},
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onState = { state ->
            when (state) {
                is AsyncImagePainter.State.Error -> onFailed()
                is AsyncImagePainter.State.Success -> {
                    val size = state.painter.intrinsicSize
                    if (size.width > 0f && size.height > 0f) onAspectRatio(size.width / size.height)
                }
                else -> Unit
            }
        },
    )
}
