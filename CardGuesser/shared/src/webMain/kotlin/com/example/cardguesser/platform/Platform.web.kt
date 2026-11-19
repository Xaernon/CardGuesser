package com.example.cardguesser.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import kotlinx.coroutines.delay

// The browser side of these calls lives in resources/platform.js.

private fun jsTilt(): Double = js("window.cardGuesser.tilt()")
private fun jsBeep(frequency: Int, millis: Int): Unit = js("window.cardGuesser.beep(frequency, millis)")
private fun jsVibrate(millis: Int): Unit = js("window.cardGuesser.vibrate(millis)")
private fun jsKeepAwake(on: Boolean): Unit = js("window.cardGuesser.keepAwake(on)")
private fun jsGetItem(key: String): String? = js("window.cardGuesser.getItem(key)")
private fun jsEmojiPng(emoji: String, pixels: Int): String = js("window.cardGuesser.emojiPng(emoji, pixels)")
private fun jsSetItem(key: String, value: String): Unit = js("window.cardGuesser.setItem(key, value)")

private fun jsFullscreen(on: Boolean): Unit = js("window.cardGuesser.fullscreen(on)")

@Composable
actual fun LandscapeMode(enabled: Boolean) {
    // Rounds go full screen and sideways where the browser allows it (Android Chrome). iPhone Safari
    // can't do either, so the player turns the phone and the layout follows (or uses the home-screen app).
    LaunchedEffect(enabled) { if (enabled) jsFullscreen(true) }
}

@Composable
actual fun KeepScreenOn() {
    DisposableEffect(Unit) {
        jsKeepAwake(true)
        onDispose { jsKeepAwake(false) }
    }
}

@Composable
actual fun TiltSensorEffect(onReading: (Float) -> Unit) {
    val currentOnReading by rememberUpdatedState(onReading)
    LaunchedEffect(Unit) {
        while (true) {
            currentOnReading(jsTilt().toFloat())
            delay(33)
        }
    }
}

private object WebGameFeedback : GameFeedback {
    override fun correct() {
        jsVibrate(90)
        jsBeep(880, 180)
    }

    override fun pass() {
        jsVibrate(160)
        jsBeep(260, 240)
    }

    override fun tick() {
        jsVibrate(25)
        jsBeep(660, 70)
    }

    override fun timeUp() {
        jsVibrate(450)
        jsBeep(440, 600)
    }
}

@Composable
actual fun rememberGameFeedback(): GameFeedback = WebGameFeedback

private object WebSettingsStore : SettingsStore {
    private const val PREFIX = "cardguesser."

    override fun getInt(key: String, default: Int) = jsGetItem(PREFIX + key)?.toIntOrNull() ?: default
    override fun putInt(key: String, value: Int) = jsSetItem(PREFIX + key, value.toString())
    override fun getBoolean(key: String, default: Boolean) =
        jsGetItem(PREFIX + key)?.toBooleanStrictOrNull() ?: default
    override fun putBoolean(key: String, value: Boolean) = jsSetItem(PREFIX + key, value.toString())
    override fun getString(key: String, default: String) = jsGetItem(PREFIX + key) ?: default
    override fun putString(key: String, value: String) = jsSetItem(PREFIX + key, value)
}

@Composable
actual fun rememberSettingsStore(): SettingsStore = remember { WebSettingsStore }

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // Screens have their own back controls; the browser back button is not intercepted.
}

@OptIn(ExperimentalEncodingApi::class, ExperimentalResourceApi::class)
@Composable
actual fun EmojiIcon(emoji: String, size: Dp, modifier: Modifier) {
    // The browser draws the emoji with the device's own emoji font and hands back a PNG.
    val bitmap = remember(emoji) {
        runCatching { Base64.decode(jsEmojiPng(emoji, 128)).decodeToImageBitmap() }.getOrNull()
    }
    if (bitmap != null) Image(bitmap, contentDescription = null, modifier = modifier.size(size * 1.25f))
}

private fun jsSetLanguage(code: String): Unit = js("window.cardGuesser.setLanguage(code)")

actual fun applyAppLanguage(code: String) = jsSetLanguage(code)

private fun jsPageBackground(css: String): Unit = js("window.cardGuesser.pageBackground(css)")

actual fun setPageBackground(argb: Int) {
    // #rrggbb of the opaque colour
    jsPageBackground("#" + (argb and 0xFFFFFF).toString(16).padStart(6, '0'))
}
