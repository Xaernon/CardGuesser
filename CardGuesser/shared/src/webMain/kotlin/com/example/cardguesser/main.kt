package com.example.cardguesser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

private fun hideLoading(): Unit = js("window.cardGuesser.ready()")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // #app is the visible part of the screen (inside the notch and Safari's bars), see index.html
    ComposeViewport(document.getElementById("app") ?: document.body!!) {
        App()
    }
    hideLoading()
}
