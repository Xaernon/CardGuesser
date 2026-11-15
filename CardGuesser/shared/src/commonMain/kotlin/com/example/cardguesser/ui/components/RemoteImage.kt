package com.example.cardguesser.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter

/** An image fetched from the internet. [onFailed] fires when it cannot be loaded (e.g. offline). */
@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    onFailed: () -> Unit = {},
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        onState = { state -> if (state is AsyncImagePainter.State.Error) onFailed() },
    )
}
