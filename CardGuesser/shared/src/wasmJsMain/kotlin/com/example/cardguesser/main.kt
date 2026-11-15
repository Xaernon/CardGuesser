package com.example.cardguesser

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document

private fun hideLoading(): Unit = js("window.cardGuesser.ready()")

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
    hideLoading()
}
