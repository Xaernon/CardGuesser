package com.example.cardguesser

import com.example.cardguesser.ui.components.CompactHeightThreshold
import com.example.cardguesser.ui.components.LocalCompactHeight
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cardguesser.data.Category
import com.example.cardguesser.data.CategoryRepository
import com.example.cardguesser.game.WordResult
import com.example.cardguesser.platform.KeepScreenOn
import com.example.cardguesser.platform.LandscapeMode
import com.example.cardguesser.platform.PlatformBackHandler
import com.example.cardguesser.platform.applyAppLanguage
import com.example.cardguesser.platform.rememberSettingsStore
import com.example.cardguesser.ui.screens.CategoriesScreen
import com.example.cardguesser.ui.screens.CountdownScreen
import com.example.cardguesser.ui.screens.GameScreen
import com.example.cardguesser.ui.screens.ResultsScreen
import com.example.cardguesser.ui.screens.SetupScreen
import com.example.cardguesser.ui.theme.AppTheme

private sealed interface Screen {
    /** Only the countdown and the round itself are played in landscape. */
    val landscape: Boolean get() = false

    data object Categories : Screen
    data class Setup(val category: Category) : Screen
    data class Countdown(val category: Category) : Screen {
        override val landscape get() = true
    }
    data class Game(val category: Category) : Screen {
        override val landscape get() = true
    }
    data class Results(val category: Category, val results: List<WordResult>) : Screen
}

@Composable
fun App() {
    val store = rememberSettingsStore()
    val settings = remember(store) { AppSettings(store) }
    var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
    var screen by remember { mutableStateOf<Screen>(Screen.Categories) }

    // Words differ per language, so the list is reloaded when the language changes.
    LaunchedEffect(settings.language) { categories = CategoryRepository.load(settings.language) }

    LandscapeMode(screen.landscape)
    // Held here rather than in each screen: screens overlap briefly while they cross-fade, so a
    // screen leaving would otherwise release the flag the next one had just taken.
    if (screen.landscape) KeepScreenOn()

    PlatformBackHandler(enabled = screen != Screen.Categories) {
        screen = when (val current = screen) {
            is Screen.Countdown -> Screen.Setup(current.category)
            is Screen.Game -> Screen.Setup(current.category)
            else -> Screen.Categories
        }
    }

    // Interface texts follow the chosen language: set it, then rebuild the screens so they reload.
    val languageCode = settings.language.code
    remember(languageCode) { applyAppLanguage(languageCode) }
    key(languageCode) {
        AppTheme(settings.palette, settings.font) {
            BoxWithConstraints(Modifier.fillMaxSize().background(settings.palette.background)) {
              CompositionLocalProvider(LocalCompactHeight provides (maxHeight < CompactHeightThreshold)) {
                AnimatedContent(
                    targetState = screen,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                ) { current ->
                    when (current) {
                        Screen.Categories -> CategoriesScreen(
                            categories,
                            settings,
                            onSelect = { screen = Screen.Setup(it) },
                        )

                        is Screen.Setup -> SetupScreen(
                            current.category,
                            settings,
                            onBack = { screen = Screen.Categories },
                            onStart = { screen = Screen.Countdown(current.category) },
                        )

                        is Screen.Countdown -> CountdownScreen(
                            onFinished = { screen = Screen.Game(current.category) },
                        )

                        is Screen.Game -> GameScreen(
                            current.category,
                            durationSeconds = settings.durationSeconds,
                            imagesEnabled = settings.imagesEnabled,
                            onFinished = { screen = Screen.Results(current.category, it) },
                            // Leaving before answering anything has no results worth showing.
                            onExit = { results ->
                                screen = if (results.isEmpty()) {
                                    Screen.Setup(current.category)
                                } else {
                                    Screen.Results(current.category, results)
                                }
                            },
                        )

                        is Screen.Results -> ResultsScreen(
                            current.category,
                            current.results,
                            onPlayAgain = { screen = Screen.Countdown(current.category) },
                            onChangeCategory = { screen = Screen.Categories },
                        )
                    }
                }
              }
            }
        }
    }
}
