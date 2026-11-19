package com.example.cardguesser.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/** While [enabled], forces landscape and hides the status bar; otherwise portrait. */
@Composable
expect fun LandscapeMode(enabled: Boolean)

/** Keeps the screen awake for as long as this is in the composition. */
@Composable
expect fun KeepScreenOn()

/**
 * Reports the gravity component along the axis coming out of the screen, in g:
 * +1 when the screen faces the sky, -1 when it faces the floor.
 */
@Composable
expect fun TiltSensorEffect(onReading: (Float) -> Unit)

/** Vibration and sound cues, for a player who cannot see the screen. */
interface GameFeedback {
    fun correct()
    fun pass()
    fun tick()
    fun timeUp()
}

@Composable
expect fun rememberGameFeedback(): GameFeedback

/** Small persistent key-value store for user settings. */
interface SettingsStore {
    fun getInt(key: String, default: Int): Int
    fun putInt(key: String, value: Int)
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
    fun getString(key: String, default: String): String
    fun putString(key: String, value: String)
}

@Composable
expect fun rememberSettingsStore(): SettingsStore

/** Intercepts the system back gesture where the platform has one. */
@Composable
expect fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit)

/**
 * Draws an emoji (category icons) at roughly [size]. Native platforms draw it as text; the web
 * canvas has no emoji font, so there the browser renders it instead.
 */
@Composable
expect fun EmojiIcon(emoji: String, size: Dp, modifier: Modifier = Modifier)

/**
 * Makes the app's own texts (menus, buttons) follow the language picked in the side menu instead of
 * the phone's language. Compose resources read the platform locale, so this sets it; App() then
 * rebuilds its screens with key(language) so every text is reloaded.
 */
expect fun applyAppLanguage(code: String)

/**
 * Colour of whatever is behind the app, for the strip the app doesn't draw (the web version stays out
 * of the camera notch, so the page shows there). Called with the full-screen colour as it changes,
 * flashes included. Only the web does anything with it; native apps draw edge to edge.
 */
expect fun setPageBackground(argb: Int)
