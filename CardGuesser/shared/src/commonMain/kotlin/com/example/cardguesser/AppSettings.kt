package com.example.cardguesser

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cardguesser.data.AppLanguage
import com.example.cardguesser.platform.SettingsStore
import com.example.cardguesser.ui.theme.AppFont
import com.example.cardguesser.ui.theme.Palette
import com.example.cardguesser.ui.theme.Palettes

/** User settings, observable from Compose and saved as they change. */
class AppSettings(private val store: SettingsStore) {
    var durationSeconds by mutableIntStateOf(
        store.getInt(KEY_DURATION, DEFAULT_DURATION).coerceIn(MIN_DURATION, MAX_DURATION)
    )
        private set

    var imagesEnabled by mutableStateOf(store.getBoolean(KEY_IMAGES, false))
        private set

    var palette by mutableStateOf(Palettes.byId(store.getString(KEY_PALETTE, Palettes.default.id)))
        private set

    var font by mutableStateOf(AppFont.byId(store.getString(KEY_FONT, AppFont.Nunito.id)))
        private set

    var language by mutableStateOf(AppLanguage.byCode(store.getString(KEY_LANGUAGE, AppLanguage.fromDevice().code)))
        private set

    val canDecreaseDuration get() = durationSeconds > MIN_DURATION
    val canIncreaseDuration get() = durationSeconds < MAX_DURATION

    fun changeDuration(steps: Int) {
        durationSeconds = (durationSeconds + steps * DURATION_STEP).coerceIn(MIN_DURATION, MAX_DURATION)
        store.putInt(KEY_DURATION, durationSeconds)
    }

    fun updateImagesEnabled(enabled: Boolean) {
        imagesEnabled = enabled
        store.putBoolean(KEY_IMAGES, enabled)
    }

    fun updatePalette(value: Palette) {
        palette = value
        store.putString(KEY_PALETTE, value.id)
    }

    fun updateFont(value: AppFont) {
        font = value
        store.putString(KEY_FONT, value.id)
    }

    fun updateLanguage(value: AppLanguage) {
        if (!value.available) return
        language = value
        store.putString(KEY_LANGUAGE, value.code)
    }

    companion object {
        const val MIN_DURATION = 30
        const val MAX_DURATION = 120
        const val DURATION_STEP = 10
        const val DEFAULT_DURATION = 60

        private const val KEY_DURATION = "duration_seconds"
        private const val KEY_IMAGES = "images_enabled"
        private const val KEY_PALETTE = "palette"
        private const val KEY_FONT = "font"
        private const val KEY_LANGUAGE = "language"
    }
}
