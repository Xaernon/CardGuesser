package com.example.cardguesser.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.cardguesser.ui.components.AppText
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType

// NOTE: the iOS side has not been compiled or run yet (the project was built on Windows).

@Composable
actual fun LandscapeMode(enabled: Boolean) {
    // TODO(iOS): rotate with UIWindowScene.requestGeometryUpdate and have the hosting
    //  view controller report the matching supportedInterfaceOrientations.
}

@Composable
actual fun KeepScreenOn() {
    DisposableEffect(Unit) {
        UIApplication.sharedApplication.idleTimerDisabled = true
        onDispose { UIApplication.sharedApplication.idleTimerDisabled = false }
    }
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun TiltSensorEffect(onReading: (Float) -> Unit) {
    val currentOnReading by rememberUpdatedState(onReading)
    val manager = remember { CMMotionManager() }
    DisposableEffect(manager) {
        manager.accelerometerUpdateInterval = 1.0 / 30.0
        manager.startAccelerometerUpdatesToQueue(NSOperationQueue.mainQueue) { data, _ ->
            // iOS reports -1 g on z when the screen faces the sky, the opposite of our convention.
            data?.acceleration?.useContents { currentOnReading(-z.toFloat()) }
        }
        onDispose { manager.stopAccelerometerUpdates() }
    }
}

private class IosGameFeedback : GameFeedback {
    private val notification = UINotificationFeedbackGenerator()
    private val impact = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)

    override fun correct() =
        notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess)

    override fun pass() =
        notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeError)

    override fun tick() = impact.impactOccurred()

    override fun timeUp() =
        notification.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeWarning)
    // TODO(iOS): sound cues.
}

@Composable
actual fun rememberGameFeedback(): GameFeedback = remember { IosGameFeedback() }

private class IosSettingsStore : SettingsStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    private fun has(key: String) = defaults.objectForKey(key) != null

    override fun getInt(key: String, default: Int) = if (has(key)) defaults.integerForKey(key).toInt() else default
    override fun putInt(key: String, value: Int) = defaults.setInteger(value.toLong(), key)
    override fun getBoolean(key: String, default: Boolean) = if (has(key)) defaults.boolForKey(key) else default
    override fun putBoolean(key: String, value: Boolean) = defaults.setBool(value, key)
    override fun getString(key: String, default: String) = defaults.stringForKey(key) ?: default
    override fun putString(key: String, value: String) = defaults.setObject(value, key)
}

@Composable
actual fun rememberSettingsStore(): SettingsStore = remember { IosSettingsStore() }

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // iOS has no system back button; screens provide their own back controls.
}

@Composable
actual fun EmojiIcon(emoji: String, size: Dp, modifier: Modifier) {
    AppText(emoji, modifier = modifier, size = size.value.sp)
}
