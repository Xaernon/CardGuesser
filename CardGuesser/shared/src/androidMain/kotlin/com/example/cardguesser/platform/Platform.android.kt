package com.example.cardguesser.platform

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.cardguesser.ui.components.AppText
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun LandscapeMode(enabled: Boolean) {
    val activity = LocalActivity.current ?: return
    DisposableEffect(activity, enabled) {
        activity.requestedOrientation = if (enabled) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val bars = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        // Only the status bar is hidden. Hiding the navigation bar as well makes Android show a
        // one-time "Viewing full screen" dialog on top of the countdown.
        if (enabled) {
            bars.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            bars.hide(WindowInsetsCompat.Type.statusBars())
        } else {
            bars.show(WindowInsetsCompat.Type.statusBars())
        }
        onDispose {}
    }
}

@Composable
actual fun KeepScreenOn() {
    val view = LocalView.current
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
}

@Composable
actual fun TiltSensorEffect(onReading: (Float) -> Unit) {
    val context = LocalContext.current
    val currentOnReading by rememberUpdatedState(onReading)
    DisposableEffect(context) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        // The gravity sensor ignores shakes; fall back to the raw accelerometer if it is missing.
        val sensor = manager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
            ?: manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // values[2] is +9.81 when the screen faces the sky.
                currentOnReading(event.values[2] / SensorManager.GRAVITY_EARTH)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
        if (sensor != null) manager?.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME)
        onDispose { manager?.unregisterListener(listener) }
    }
}

private class AndroidGameFeedback(context: Context) : GameFeedback {
    private val vibrator = context.getSystemService(Vibrator::class.java)

    // Creating a tone generator can fail when the audio system is busy; sounds are optional.
    private val tones = runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, 85) }.getOrNull()

    override fun correct() {
        vibrate(longArrayOf(0, 90))
        tone(ToneGenerator.TONE_PROP_ACK, 180)
    }

    override fun pass() {
        vibrate(longArrayOf(0, 60, 90, 60))
        tone(ToneGenerator.TONE_PROP_NACK, 220)
    }

    override fun tick() {
        vibrate(longArrayOf(0, 25))
        tone(ToneGenerator.TONE_PROP_BEEP, 80)
    }

    override fun timeUp() {
        vibrate(longArrayOf(0, 450))
        tone(ToneGenerator.TONE_PROP_BEEP2, 500)
    }

    fun release() {
        tones?.release()
    }

    private fun vibrate(pattern: LongArray) {
        runCatching { vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1)) }
    }

    private fun tone(type: Int, millis: Int) {
        runCatching { tones?.startTone(type, millis) }
    }
}

@Composable
actual fun rememberGameFeedback(): GameFeedback {
    val context = LocalContext.current.applicationContext
    val feedback = remember(context) { AndroidGameFeedback(context) }
    DisposableEffect(feedback) { onDispose { feedback.release() } }
    return feedback
}

private class AndroidSettingsStore(context: Context) : SettingsStore {
    private val prefs = context.getSharedPreferences("cardguesser_settings", Context.MODE_PRIVATE)

    override fun getInt(key: String, default: Int) = prefs.getInt(key, default)
    override fun putInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    override fun getBoolean(key: String, default: Boolean) = prefs.getBoolean(key, default)
    override fun putBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    override fun getString(key: String, default: String) = prefs.getString(key, default) ?: default
    override fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
}

@Composable
actual fun rememberSettingsStore(): SettingsStore {
    val context = LocalContext.current.applicationContext
    return remember(context) { AndroidSettingsStore(context) }
}

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}

@Composable
actual fun EmojiIcon(emoji: String, size: Dp, modifier: Modifier) {
    AppText(emoji, modifier = modifier, size = size.value.sp)
}

private var chosenLocale: java.util.Locale? = null

actual fun applyAppLanguage(code: String) {
    chosenLocale = java.util.Locale.forLanguageTag(code)
    reapplyAppLanguage()
}

/**
 * Android resets the default language to the phone's on every configuration change (rotating into a
 * round, for one), which turned the texts back to English. MainActivity calls this from
 * onConfigurationChanged, before the screens are told about the change, so they keep the game's language.
 */
fun reapplyAppLanguage() {
    val locale = chosenLocale ?: return
    java.util.Locale.setDefault(locale)
    android.os.LocaleList.setDefault(android.os.LocaleList(locale))
}

actual fun setPageBackground(argb: Int) = Unit
